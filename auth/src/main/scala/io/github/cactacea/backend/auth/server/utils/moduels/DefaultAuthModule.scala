package io.github.cactacea.backend.auth.server.utils.moduels

import com.google.inject.name.Named
import com.google.inject.{Provides, Singleton}
import com.twitter.finatra.httpclient.HttpClient
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.auth.core.domain.models.Authentication
import io.github.cactacea.backend.auth.core.domain.repositories.{AuthenticationsRepository, OAuth2Repository, PasswordsRepository}
import io.github.cactacea.backend.auth.core.utils.providers.EmailsProvider
import io.github.cactacea.backend.auth.server.utils.providers.OAuth2RequestProvider
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.filhouette.api.actions._
import io.github.cactacea.filhouette.api.crypto.CrypterAuthenticatorEncoder
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.api.services.IdentityService
import io.github.cactacea.filhouette.api.util._
import io.github.cactacea.filhouette.impl.authenticators._
import io.github.cactacea.filhouette.impl.crypto.{JcaCrypter, JcaCrypterSettings, JcaSigner, JcaSignerSettings}
import io.github.cactacea.filhouette.impl.providers._
import io.github.cactacea.filhouette.impl.providers.oauth2.{FacebookClientModule, FacebookProvider, GoogleClientModule, GoogleProvider}
import io.github.cactacea.filhouette.impl.providers.state.{CsrfStateItemHandler, CsrfStateSettings}
import io.github.cactacea.filhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import io.github.cactacea.filhouette.password.{BCryptPasswordHasher, BCryptSha256PasswordHasher}
import io.github.cactacea.filhouette.persistence.daos.DelegableAuthInfoDAO
import io.github.cactacea.filhouette.persistence.repositories.DelegableAuthInfoRepository

object DefaultAuthModule extends TwitterModule {

  override val modules = Seq(new FacebookClientModule(), new GoogleClientModule())
  /**
    * Configures the module.
    */
  override def configure(): Unit = {
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[Clock].toInstance(Clock())
    bindSingleton[IdentityService[Authentication]].to[AuthenticationsRepository]
    bindSingleton[DelegableAuthInfoDAO[PasswordInfo]].to[PasswordsRepository]
    bindSingleton[DelegableAuthInfoDAO[OAuth2Info]].to[OAuth2Repository]
  }

  /**
    * Provides the crypter for the authenticator.
    *
    * @return The crypter for the authenticator.
    */
  @Provides
  @Singleton
  def provideAuthenticatorCrypter(): JcaCrypter = {
    new JcaCrypter(JcaCrypterSettings(Config.crypter.crypterKey))
  }

  /**
    * Provides the signer for the authenticator.
    *
    * @return The signer for the authenticator.
    */
  @Provides
  def provideAuthenticatorSigner(): JcaSigner = {
    new JcaSigner(JcaSignerSettings(Config.crypter.signerKey))
  }

  /**
    * Provides the auth info repository.
    *
    * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
    * @return The auth info repository instance.
    */
  @Provides
  @Singleton
  def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo], oauth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info]): AuthInfoRepository = {
    new DelegableAuthInfoRepository(passwordInfoDAO, oauth2InfoDAO)
  }

  /**
    * Provides the authenticator service.
    *
    * @param crypter The crypter implementation.
    * @param idGenerator The ID generator implementation.
    * @param clock The clock instance.
    * @return The authenticator service.
    */
  @Provides
  @Singleton
  def provideJWTAuthenticatorService(
                                   crypter: JcaCrypter,
                                   idGenerator: IDGenerator,
                                   clock: Clock
                                 ): JWTAuthenticatorService = {

    val settings = JWTAuthenticatorSettings(fieldName = Config.auth.headerNames.authorizationKey, sharedSecret = Config.crypter.signerKey)
    val encoder = new CrypterAuthenticatorEncoder(crypter)
    new JWTAuthenticatorService(settings, None, encoder, idGenerator, clock)
  }

  /**
    * Provides the password hasher registry.
    *
    * @return The password hasher registry.
    */
  @Provides
  @Singleton
  def providePasswordHasherRegistry(): PasswordHasherRegistry = {
    PasswordHasherRegistry(new BCryptSha256PasswordHasher(), Seq(new BCryptPasswordHasher()))
  }

  /**
    * Provides the credentials provider.
    *
    * @param authInfoRepository The auth info repository implementation.
    * @param passwordHasherRegistry The password hasher registry.
    * @return The credentials provider.
    */
  @Provides
  @Singleton
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository, passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider = {
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }

  /**
    * Provides the emails provider.
    *
    * @param authInfoRepository The auth info repository implementation.
    * @param passwordHasherRegistry The password hasher registry.
    * @return The credentials provider.
    */
  @Provides
  @Singleton
  def provideEmailsProvider(
                                  authInfoRepository: AuthInfoRepository,
                                  passwordHasherRegistry: PasswordHasherRegistry): EmailsProvider = {

    new EmailsProvider(authInfoRepository, passwordHasherRegistry)
  }

  @Provides
  @Singleton
  def provideSecuredAction(): DefaultSecuredAction = {
    val securedErrorHandler = new DefaultSecuredErrorHandler()
    val securedRequestHandler = new DefaultSecuredRequestHandler(securedErrorHandler)
    new DefaultSecuredAction(securedRequestHandler)
  }

  @Provides
  @Singleton
  def provideSecuredActionBuilder(
                                   identityService: IdentityService[Authentication],
                                   authenticatorService: JWTAuthenticatorService,
                                   oauth2RequestProvider: OAuth2RequestProvider
                                 ): SecuredActionBuilder[Authentication, JWTAuthenticator] = {
    val securedErrorHandler = new DefaultSecuredErrorHandler()
    val securedRequestHandler = new DefaultSecuredRequestHandler(securedErrorHandler)
    val securedAction = new DefaultSecuredAction(securedRequestHandler)
    securedAction(identityService, authenticatorService, Seq(oauth2RequestProvider))
  }


  /**
    * Provides the CSRF state item handler.
    *
    * @param idGenerator The ID generator implementation.
    * @param signer The signer implementation.
    * @return The CSRF state item implementation.
    */
  @Provides
  def provideCsrfStateItemHandler(idGenerator: IDGenerator, signer: JcaSigner): CsrfStateItemHandler = {
    val settings = CsrfStateSettings()
    new CsrfStateItemHandler(settings, idGenerator, signer)
  }

  /**
    * Provides the social state handler.
    *
    * @param signer The signer implementation.
    * @return The social state handler implementation.
    */
  @Provides
  def provideSocialStateHandler(
                                 signer: JcaSigner,
                                 csrfStateItemHandler: CsrfStateItemHandler): SocialStateHandler = {

    new DefaultSocialStateHandler(Set(csrfStateItemHandler), signer)
  }

  /**
    * Provides the Facebook provider.
    *
    * @param socialStateHandler The social state handler implementation.
    * @return The Facebook provider.
    */
  @Provides
  def provideFacebookProvider(socialStateHandler: SocialStateHandler, @Named("FacebookHttpClient") httpClient: HttpClient): FacebookProvider = {

    val settings = OAuth2Settings(
      authorizationURL = Config.facebook.authorizationURL,
      accessTokenURL = Config.facebook.accessTokenURL,
      redirectURL = Config.facebook.redirectURL,
      clientID = Config.facebook.clientID,
      clientSecret = Config.facebook.clientSecret,
      scope = Config.facebook.scope
    )

    new FacebookProvider(socialStateHandler, settings, httpClient)
  }


  /**
    * Provides the Facebook provider.
    *
    * @param socialStateHandler The social state handler implementation.
    * @return The Google provider.
    */
  @Provides
  def provideGoogleProvider(socialStateHandler: SocialStateHandler, @Named("GoogleHttpClient") httpClient: HttpClient): GoogleProvider = {

    val settings = OAuth2Settings(
      authorizationURL = Config.google.authorizationURL,
      accessTokenURL = Config.google.accessTokenURL,
      redirectURL = Config.google.redirectURL,
      clientID = Config.google.clientID,
      clientSecret = Config.google.clientSecret,
      scope = Config.google.scope
    )

    new GoogleProvider(socialStateHandler, settings, httpClient)
  }

  /**
    * Provides the social provider registry.
    *
    * @return The SocialProviderRegistry
    */
  @Provides
  def provideSocialProviderRegistry(facebookProvider: FacebookProvider, googleProvider: GoogleProvider): SocialProviderRegistry = {
    SocialProviderRegistry(Seq(facebookProvider, googleProvider))
  }




  //  /**
  //    * Provides the OAuth1 token secret provider.
  //    *
  //    * @param signer The signer implementation.
  //    * @param crypter The crypter implementation.
  //    * @param configuration The Play configuration.
  //    * @param clock The clock instance.
  //    * @return The OAuth1 token secret provider implementation.
  //    */
  //  @Provides
  //  def provideOAuth1TokenSecretProvider(
  //                                        @Named("oauth1-token-secret-signer") signer: Signer,
  //                                        @Named("oauth1-token-secret-crypter") crypter: Crypter,
  //                                        configuration: Configuration,
  //                                        clock: Clock): OAuth1TokenSecretProvider = {
  //
  //    val settings = configuration.underlying.as[CookieSecretSettings]("silhouette.oauth1TokenSecretProvider")
  //    new CookieSecretProvider(settings, signer, crypter, clock)
  //  }

  //  /**
  //    * Provides the Twitter provider.
  //    *
  //    * @param httpLayer The HTTP layer implementation.
  //    * @param tokenSecretProvider The token secret provider implementation.
  //    * @param configuration The Play configuration.
  //    * @return The Twitter provider.
  //    */
  //  @Provides
  //  def provideTwitterProvider(
  //                              httpLayer: HTTPLayer,
  //                              tokenSecretProvider: OAuth1TokenSecretProvider,
  //                              configuration: Configuration): TwitterProvider = {
  //
  //    val settings = configuration.underlying.as[OAuth1Settings]("silhouette.twitter")
  //    new TwitterProvider(, new PlayOAuth1Service(settings), tokenSecretProvider, settings)
  //  }



}
