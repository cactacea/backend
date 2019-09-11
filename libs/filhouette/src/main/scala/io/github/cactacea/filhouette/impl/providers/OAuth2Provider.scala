package io.github.cactacea.filhouette.impl.providers

import java.net.URLEncoder.encode

import com.twitter.finagle.http._
import com.twitter.util.{Future, Return, Throw, Try}
import io.github.cactacea.filhouette.api.exceptions.ConfigurationException
import io.github.cactacea.filhouette.api.util.RequestExtractor._
import io.github.cactacea.filhouette.api.{AuthInfo, Logger}
import io.github.cactacea.filhouette.impl.exceptions.{AccessDeniedException, UnexpectedResponseException}
import io.github.cactacea.filhouette.impl.providers.OAuth2Provider._
import io.github.cactacea.filhouette.impl.providers.state.UserStateItemHandler
import io.github.cactacea.filhouette.impl.util.Json

import scala.reflect.ClassTag

/**
  * The OAuth2 info.
  *
  * @param accessToken  The access token.
  * @param tokenType    The token type.
  * @param expiresIn    The number of seconds before the token expires.
  * @param refreshToken The refresh token.
  * @param params       Additional params transported in conjunction with the token.
  */
case class OAuth2Info(
                       accessToken: String,
                       tokenType: Option[String] = None,
                       expiresIn: Option[Int] = None,
                       refreshToken: Option[String] = None,
                       params: Option[Map[String, String]] = None) extends AuthInfo

/**
  * The Oauth2 info companion object.
  */
object OAuth2Info extends OAuth2Constants

/**
  * Base implementation for all OAuth2 providers.
  */
trait OAuth2Provider extends SocialStateProvider with OAuth2Constants with Logger {

  /**
    * The type of the auth info.
    */
  type A = OAuth2Info

  /**
    * The settings type.
    */
  type Settings = OAuth2Settings

  /**
    * The social state handler implementation.
    */
  protected val stateHandler: SocialStateHandler

  /**
    * A list with headers to send to the API.
    */
  protected val headers: Seq[(String, String)] = Seq()

  /**
    * The default access token response code.
    *
    * Override this if a specific provider uses another HTTP status code for a successful access token response.
    */
  protected val accessTokeResponseCode: Int = 200

  /**
    * Starts the authentication process.
    *
    * @param request The current request.
    * @return Either a Response or the auth info from the provider.
    */
  def authenticate()(implicit request: Request): Future[Either[Response, OAuth2Info]] = {
    handleFlow(handleAuthorizationFlow(stateHandler)) { code =>
      stateHandler.unserialize(request.extractString(State).getOrElse("")).flatMap { _ =>
        getAccessToken(code).map(oauth2Info => oauth2Info)
      }
    }
  }

  /**
    * Authenticates the user and returns the auth information and the user state.
    *
    * Returns either a [[StatefulAuthInfo]] if all went OK or a `play.api.mvc.Result` that the controller
    * sends to the browser (e.g.: in the case of OAuth where the user needs to be redirected to the service
    * provider).
    *
    * @tparam S The type of the user state.
    * @param request The request.
    * @return Either a Response or the [[StatefulAuthInfo]] from the provider.
    */
    def authenticate[S <: SocialStateItem](userState: S)(
      implicit
      //    format: Format[S],
      classTag: ClassTag[S],
      request: Request
    ): Future[Either[Response, StatefulAuthInfo[A, S]]] = {
    val userStateItemHandler = new UserStateItemHandler(userState)
    val newStateHandler = stateHandler.withHandler(userStateItemHandler)

    handleFlow(handleAuthorizationFlow(newStateHandler)) { code =>
      newStateHandler.unserialize(request.extractString(State).getOrElse("")).flatMap { state =>
        val maybeUserState: Option[S] = state.items.flatMap(item => userStateItemHandler.canHandle(item)).headOption
        maybeUserState match {
          case Some(s) => getAccessToken(code).map(oauth2Info => StatefulAuthInfo(oauth2Info, s))
          case None    => Future.exception(new UnexpectedResponseException("Cannot extract user info from response"))
        }
      }
    }
  }

  /**
    * Handles the OAuth2 flow.
    *
    * The left flow is the authorization flow, which will be processed, if no `code` parameter exists
    * in the request. The right flow is the access token flow, which will be executed after a successful
    * authorization.
    *
    * @param left The authorization flow.
    * @param right The access token flow.
    * @param request The request.
    * @tparam L The return type of the left flow.
    * @tparam R The return type of the right flow.
    * @return Either the left or the right flow.
    */
  def handleFlow[L, R](left: => Future[L])(right: String => Future[R])(
    implicit
    request: Request
  ): Future[Either[L, R]] = {
    request.extractString(Error).map {
      case e @ AccessDenied => new AccessDeniedException(AuthorizationError.format(id, e))
      case e                => new UnexpectedResponseException(AuthorizationError.format(id, e))
    } match {
      case Some(throwable) => Future.exception(throwable)
      case None => request.extractString(Code) match {
        // We're being redirected back from the authorization server with the access code and the state
        case Some(code) => right(code).map(Right.apply)
        // There's no code in the request, this is the first step in the OAuth flow
        case None       => left.map(Left.apply)
      }
    }
  }

  /**
    * Handles the authorization step of the OAuth2 flow.
    *
    * @param stateHandler The state handler to use.
    * @param request The request.
    * @return The redirect to the authorization URL of the OAuth2 provider.
    */
  protected def handleAuthorizationFlow(stateHandler: SocialStateHandler)(
    implicit
    request: Request
  ): Future[Response] = {
    stateHandler.state.map { state =>
      val serializedState = stateHandler.serialize(state)
      val stateParam = if (serializedState.isEmpty) List() else List(State -> serializedState)
      val redirectParam = settings.redirectURL match {
        case Some(rUri) => List((RedirectURI, resolveCallbackURL(rUri)))
        case None       => Nil
      }
      val params = settings.scope.foldLeft(List(
        (ClientID, settings.clientID),
        (ResponseType, Code)) ++ stateParam ++ settings.authorizationParams.toList ++ redirectParam) {
        case (p, s) => (Scope, s) :: p
      }
      val encodedParams = params.map { p => encode(p._1, "UTF-8") + "=" + encode(p._2, "UTF-8") }
      val url = settings.authorizationURL.getOrElse {
        Future.exception(new ConfigurationException(AuthorizationURLUndefined.format(id)))
      } + encodedParams.mkString("?", "&", "")
      val response = Response(Status.TemporaryRedirect)
      response.location = url
      val redirect = stateHandler.publish(response, state)
      logger.debug("[Filhouette][%s] Use authorization URL: %s".format(id, settings.authorizationURL))
      logger.debug("[Filhouette][%s] Redirecting to: %s".format(id, url))
      redirect
    }
  }

  /**
    * Gets the access token.
    *
    * @param code    The access code.
    * @param request The current request.
    * @return The info containing the access token.
    */
  protected def getAccessToken(code: String)(implicit request: Request): Future[OAuth2Info] = {
    val redirectParam = settings.redirectURL match {
      case Some(rUri) => List((RedirectURI, resolveCallbackURL(rUri)))
      case None       => Nil
    }
    val params: Map[String, String] = Map(
      ClientID -> settings.clientID,
      ClientSecret -> settings.clientSecret,
      GrantType -> AuthorizationCode,
      Code -> code) ++ settings.accessTokenParams ++ (redirectParam.toMap[String, String])

    val r = RequestBuilder().url(Request.queryString(settings.accessTokenURL, params)).build(Method.Post, None)
    headers.foreach({ case (k, v) => request.headerMap.add(k, v) })

    httpClient.execute(r).flatMap({ response =>
      logger.debug("[Filhouette][%s] Access token response: [%s]".format(id, response.contentString))
      Future.const(buildInfo(response))
    })
  }

  /**
    * Builds the OAuth2 info from response.
    *
    * @param response The response from the provider.
    * @return The OAuth2 info on success, otherwise a failure.
    */
  protected def buildInfo(response: Response): Try[OAuth2Info] = {
    response.status match {
      case status if status == accessTokeResponseCode =>
        Try(response.contentString) match {
          case Return(json) =>
            Try(Json.validate[OAuth2Info](json)).transform(_ match {
              case Return(i) => Return(i)
              case Throw(error) => Throw(new UnexpectedResponseException(InvalidInfoFormat.format(id, error)))
            })
          case Throw(error) => Throw(
            new UnexpectedResponseException(JsonParseError.format(id, response.contentString, error))
          )
        }
      case status => Throw(
        new UnexpectedResponseException(UnexpectedResponse.format(id, response.contentString, status))
      )
    }
  }
}

/**
  * The OAuth2Provider companion object.
  */
object OAuth2Provider extends OAuth2Constants {

  /**
    * The error messages.
    */
  val AuthorizationURLUndefined = "[Filhouette][%s] Authorization URL is undefined"
  val AuthorizationError = "[Filhouette][%s] Authorization server returned error: %s"
  val InvalidInfoFormat = "[Filhouette][%s] Cannot build OAuth2Info because of invalid response format: %s"
  val JsonParseError = "[Filhouette][%s] Cannot parse response `%s` to Json; got error: %s"
  val UnexpectedResponse = "[Filhouette][%s] Got unexpected response `%s`; status code: %s"
}

/**
  * The OAuth2 constants.
  */
trait OAuth2Constants {

  val ClientID = "client_id"
  val ClientSecret = "client_secret"
  val RedirectURI = "redirect_uri"
  val Scope = "scope"
  val ResponseType = "response_type"
  val State = "state"
  val GrantType = "grant_type"
  val AuthorizationCode = "authorization_code"
  val AccessToken = "access_token"
  val Error = "error"
  val Code = "code"
  val TokenType = "token_type"
  val ExpiresIn = "expires_in"
  val Expires = "expires"
  val RefreshToken = "refresh_token"
  val AccessDenied = "access_denied"
}

/**
  * The OAuth2 settings.
  *
  * @param authorizationURL    The authorization URL provided by the OAuth provider.
  * @param accessTokenURL      The access token URL provided by the OAuth provider.
  * @param redirectURL         The redirect URL to the application after a successful authentication on the OAuth
  *                            provider. The URL can be a relative path which will be resolved against the current
  *                            request's host.
  * @param apiURL              The URL to fetch the profile from the API. Can be used to override the default URL
  *                            hardcoded in every provider implementation.
  * @param clientID            The client ID provided by the OAuth provider.
  * @param clientSecret        The client secret provided by the OAuth provider.
  * @param scope               The OAuth2 scope parameter provided by the OAuth provider.
  * @param authorizationParams Additional params to add to the authorization request.
  * @param accessTokenParams   Additional params to add to the access token request.
  * @param customProperties    A map of custom properties for the different providers.
  */
case class OAuth2Settings(
                           authorizationURL: Option[String] = None,
                           accessTokenURL: String,
                           redirectURL: Option[String] = None,
                           apiURL: Option[String] = None,
                           clientID: String, clientSecret: String,
                           scope: Option[String] = None,
                           authorizationParams: Map[String, String] = Map.empty,
                           accessTokenParams: Map[String, String] = Map.empty,
                           customProperties: Map[String, String] = Map.empty
                         )
