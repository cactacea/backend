package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.{AccountStatusType, SocialAccountType}
import io.github.cactacea.core.domain.models._
import io.github.cactacea.core.util.clients.facebook.FacebookHttpClient
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.models.Accounts
import io.github.cactacea.core.infrastructure.clients.google.GooglePlusHttpClient
import io.github.cactacea.core.infrastructure.clients.twitter.TwitterHttpClient
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.tokens.AuthTokenGenerator
import org.joda.time.DateTime

@Singleton
class SessionRepository {

  @Inject var advertisementSettingsDAO: AdvertisementSettingsDAO = _
  @Inject var devicesDAO: DevicesDAO = _
  @Inject var notificationSettingsDAO: PushNotificationSettingsDAO = _
  @Inject var feedsDAO: FeedsDAO = _
  @Inject var feedFavoritesDAO: FeedFavoritesDAO = _
  @Inject var accountsDAO: AccountsDAO = _
  @Inject var socialAccountsDAO: SocialAccountsDAO = _
  @Inject var validationDAO: ValidationDAO = _

  @Inject var googlePlusHttpClient: GooglePlusHttpClient = _
  @Inject var twitterHttpClient: TwitterHttpClient = _
  @Inject var facebookHttpClient: FacebookHttpClient = _

  def signUp(accountName: String, displayName: String, password: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
    for {
      _ <- validationDAO.notExistAccountName(accountName)
      r <- _signUp(accountName, displayName, password, udid, web, birthday, location, bio, userAgent)
    } yield (r)
  }

  def signIn(accountName: String, password: String, udid: String, userAgent: String): Future[Authentication] = {
    accountsDAO.find(accountName, password).flatMap(_ match {
      case Some(a) =>
        if (a.isTerminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else {
          _signIn(a, udid, userAgent)
        }
      case None =>
        Future.exception(CactaceaException(InvalidAccountNameOrPassword))
    })
  }

  def signOut(udid: String, sessionId: SessionId): Future[Boolean] = {
    for {
      a <- accountsDAO.signOut(sessionId)
      b <- devicesDAO.delete(udid, sessionId)
    } yield (a && b)
  }


  def signUp(accountType: SocialAccountType, accountName: String, displayName: String, password: String, accessTokenKey: String, accessTokenSecret: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
    val client = accountType match {
      case SocialAccountType.facebook => facebookHttpClient
      case SocialAccountType.google => googlePlusHttpClient
      case SocialAccountType.twitter => twitterHttpClient
    }
    client.get(accessTokenKey, accessTokenSecret).flatMap(_ match {
      case Some(p) =>
        _signUp(accountType, accountName: String, displayName, password, p.accountId, udid, web, birthday, location, bio, userAgent)
      case None =>
        Future.exception(CactaceaException(SessionTimeout))
    })
  }

  private def _signUp(socialAccountType: SocialAccountType, accountName: String, displayName: String, password: String, socialAccountId: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
    socialAccountsDAO.find(socialAccountType, socialAccountId).flatMap(_ match {
      case Some(sa) =>
        accountsDAO.find(sa.accountId.toSessionId).flatMap(_ match {
          case Some(a) =>
            if (a.isTerminated) {
              Future.exception(CactaceaException(AccountTerminated))
            } else {
              _signIn(a, udid, userAgent)
            }
          case None =>
            Future.exception(CactaceaException(AccountNotFound))
        })
      case None =>
        _signUp(accountName, displayName, password, udid, web, birthday, location, bio, userAgent).flatMap({ a =>
            socialAccountsDAO.create(socialAccountType, socialAccountId, a.account.id.toSessionId).flatMap(_ => Future.value(a))
        })
    })
  }

  private def _signUp(accountName: String, displayName: String, password: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
    (for {
      accountId <- accountsDAO.create(accountName, displayName, password, web, birthday, location, bio)
      sessionId = accountId.toSessionId
      _             <- devicesDAO.create(udid, Some(userAgent), sessionId)
      _             <- notificationSettingsDAO.create(true, true, true, true, true, true, sessionId)
      _             <- advertisementSettingsDAO.create(true, true, true, true, true, sessionId)
      account       <- accountsDAO.find(sessionId)
    } yield (accountId, account)).flatMap( _ match {
      case (accountId, Some(a)) =>
        val accessToken = AuthTokenGenerator.generate(accountId.value, udid)
        val session = Account(a)
        Future.value(Authentication(session, accessToken))
      case (_, None) =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }


  def signIn(socialAccountType: SocialAccountType, accessTokenKey: String, accessTokenSecret: String, udid: String, userAgent: String): Future[Authentication] = {

    socialAccountType match {
      case SocialAccountType.facebook =>
        facebookHttpClient.get(accessTokenKey, accessTokenSecret).flatMap(_ match {
          case Some(p) =>
            _signIn(SocialAccountType.facebook, p.accountId, udid, userAgent)
          case None =>
            Future.exception(CactaceaException(SessionTimeout))
        })

      case SocialAccountType.google =>
        googlePlusHttpClient.get(accessTokenKey, accessTokenSecret).flatMap(_ match {
          case Some(p) =>
            _signIn(SocialAccountType.google, p.accountId, udid, userAgent)
          case None =>
            Future.exception(CactaceaException(SessionTimeout))
        })

      case SocialAccountType.twitter =>
        twitterHttpClient.get(accessTokenSecret, accessTokenSecret).flatMap(_ match {
          case Some(p) =>
            _signIn(SocialAccountType.twitter, p.accountId, udid, userAgent)
          case None =>
            Future.exception(CactaceaException(SessionTimeout))
        })
    }
  }


  private def _signIn(socialAccountType: SocialAccountType, socialAccountId: String, udid: String, userAgent: String): Future[Authentication] = {
    socialAccountsDAO.find(socialAccountType, socialAccountId).flatMap(_ match {
      case Some(sa) =>
        accountsDAO.find(sa.accountId.toSessionId).flatMap(_ match {
          case Some(a) =>
            if (a.isTerminated) {
              Future.exception(CactaceaException(AccountTerminated))
            } else {
              _signIn(a, udid, userAgent)
            }
          case None =>
            Future.exception(CactaceaException(AccountNotFound))
        })
      case None =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }

  private def _signIn(a: Accounts, udid: String, userAgent: String): Future[Authentication] = {
    devicesDAO.exist(a.id.toSessionId, udid).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        devicesDAO.create(udid, Some(userAgent), a.id.toSessionId)
    }).map({ _ =>
      val accessToken = AuthTokenGenerator.generate(a.id.value, udid)
      val account = Account(a)
      Authentication(account, accessToken)
    })
  }



  def checkAccountStatus(sessionId: SessionId, expiresIn: Long): Future[Boolean] = {
    accountsDAO.findStatus(sessionId).flatMap( _ match {
      case Some((accountStatusType, signedOutAt)) =>
        if (accountStatusType == AccountStatusType.deleted) {
          Future.exception(CactaceaException(AccountDeleted))
        } else if (accountStatusType == AccountStatusType.terminated) {
          Future.exception(CactaceaException(AccountTerminated))
        } else if (signedOutAt.map(_ > expiresIn).getOrElse(false)) {
          Future.exception(CactaceaException(SessionTimeout))
        } else {
          Future.True
        }
      case None =>
        Future.exception(CactaceaException(SessionNotAuthorized))
    })
  }


}
