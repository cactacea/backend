package io.github.cactacea.core.domain.repositories

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import io.github.cactacea.core.domain.enums.AccountStatusType
import io.github.cactacea.core.domain.models._
import io.github.cactacea.core.infrastructure.dao._
import io.github.cactacea.core.infrastructure.identifiers.SessionId
import io.github.cactacea.core.infrastructure.models.Accounts
import io.github.cactacea.core.util.exceptions.CactaceaException
import io.github.cactacea.core.util.responses.CactaceaError._
import io.github.cactacea.core.util.tokens.AuthTokenGenerator
import org.joda.time.DateTime

@Singleton
class SessionsRepository @Inject()(
                                   advertisementSettingsDAO: AdvertisementSettingsDAO,
                                   devicesDAO: DevicesDAO,
                                   notificationSettingsDAO: PushNotificationSettingsDAO,
                                   feedsDAO: FeedsDAO,
                                   feedFavoritesDAO: FeedFavoritesDAO,
                                   accountsDAO: AccountsDAO,
                                   socialAccountsDAO: SocialAccountsDAO,
                                   validationDAO: ValidationDAO,
//                                   googlePlusHttpClient: GooglePlusHttpClient,
//                                   twitterHttpClient: TwitterHttpClient,
//                                   facebookHttpClient: FacebookHttpClient,
                                   authTokenGenerator: AuthTokenGenerator) {

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


  def signUp(socialAccountType: String, accountName: String, displayName: String, password: String, socialAccountId: String, accessTokenKey: String, accessTokenSecret: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
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

    //    _signUp(socialAccountType, accountName, displayName, password, socialAccountId, udid, web, birthday, location, bio, userAgent)
//    val client = accountType match {
//      case "facebook" => facebookHttpClient
//      case "google" => googlePlusHttpClient
//      case "twitter" => twitterHttpClient
//    }
//    client.get(accessTokenKey, accessTokenSecret).flatMap(_ match {
//      case Some(p) =>
//        _signUp(accountType, accountName, displayName, password, p.accountId, udid, web, birthday, location, bio, userAgent)
//      case None =>
//        Future.exception(CactaceaException(SessionTimeout))
//    })
  }

//  private def _signUp(socialAccountType: String, accountName: String, displayName: String, password: String, socialAccountId: String, udid: String, web: Option[String], birthday: Option[DateTime], location: Option[String], bio: Option[String], userAgent: String): Future[Authentication] = {
//  }

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
        val accessToken = authTokenGenerator.generate(accountId.value, udid)
        val session = Account(a)
        Future.value(Authentication(session, accessToken))
      case (_, None) =>
        Future.exception(CactaceaException(AccountNotFound))
    })
  }


  def signIn(socialAccountType: String, socialAccountId: String, accessTokenKey: String, accessTokenSecret: String, udid: String, userAgent: String): Future[Authentication] = {
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
//    socialAccountType match {
//      case "facebook" =>
//        facebookHttpClient.get(accessTokenKey, accessTokenSecret).flatMap(_ match {
//          case Some(p) =>
//            _signIn("facebook", p.accountId, udid, userAgent)
//          case None =>
//            Future.exception(CactaceaException(SessionTimeout))
//        })
//
//      case "google" =>
//        googlePlusHttpClient.get(accessTokenKey, accessTokenSecret).flatMap(_ match {
//          case Some(p) =>
//            _signIn("google", p.accountId, udid, userAgent)
//          case None =>
//            Future.exception(CactaceaException(SessionTimeout))
//        })
//
//      case "twitter" =>
//        twitterHttpClient.get(accessTokenSecret, accessTokenSecret).flatMap(_ match {
//          case Some(p) =>
//            _signIn("twitter", p.accountId, udid, userAgent)
//          case None =>
//            Future.exception(CactaceaException(SessionTimeout))
//        })
//    }
  }


  private def _signIn(a: Accounts, udid: String, userAgent: String): Future[Authentication] = {
    devicesDAO.exist(a.id.toSessionId, udid).flatMap(_ match {
      case true =>
        Future.True
      case false =>
        devicesDAO.create(udid, Some(userAgent), a.id.toSessionId)
    }).map({ _ =>
      val accessToken = authTokenGenerator.generate(a.id.value, udid)
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
