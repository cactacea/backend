package io.github.cactacea.backend.utils.auth

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.filhouette.api.actions._
import io.github.cactacea.filhouette.api.crypto.CrypterAuthenticatorEncoder
import io.github.cactacea.filhouette.api.repositories.AuthInfoRepository
import io.github.cactacea.filhouette.api.services.IdentityService
import io.github.cactacea.filhouette.api.util._
import io.github.cactacea.filhouette.impl.authenticators._
import io.github.cactacea.filhouette.impl.crypto.{JcaCrypter, JcaCrypterSettings}
import io.github.cactacea.filhouette.impl.providers._
import io.github.cactacea.filhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import io.github.cactacea.filhouette.password.{BCryptPasswordHasher, BCryptSha256PasswordHasher}
import io.github.cactacea.filhouette.persistence.daos.DelegableAuthInfoDAO
import io.github.cactacea.filhouette.persistence.repositories.DelegableAuthInfoRepository

object CactaceaAuthModule extends TwitterModule {

  /**
    * Configures the module.
    */
  override def configure(): Unit = {
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[Clock].toInstance(Clock())
    bindSingleton[IdentityService[CactaceaAccount]].to[CactaceaIdentityService]
    bindSingleton[DelegableAuthInfoDAO[PasswordInfo]].to[CactaceaAuthenticationsRepository]
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
    * Provides the auth info repository.
    *
    * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
    * @return The auth info repository instance.
    */
  @Provides
  @Singleton
  def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository = {
    new DelegableAuthInfoRepository(passwordInfoDAO)
  }

  /**
    * Provides the authenticator service.
    *
//    * @param cookieSigner The cookie signer implementation.
    * @param crypter The crypter implementation.
    * @param idGenerator The ID generator implementation.
    * @param clock The clock instance.
    * @return The authenticator service.
    */
  @Provides
  @Singleton
  def provideJWTAuthenticatorService(
//                                   cookieSigner: JcaSigner,
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
  def provideCredentialsProvider(
                                  authInfoRepository: AuthInfoRepository,
                                  passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider = {

    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }

  @Provides
  @Singleton
  def provideSecuredActionBuilder(
                               identityService: IdentityService[CactaceaAccount],
                               authenticatorService: JWTAuthenticatorService): SecuredActionBuilder[CactaceaAccount, JWTAuthenticator] = {
    val securedErrorHandler = new DefaultSecuredErrorHandler()
    val securedRequestHandler = new DefaultSecuredRequestHandler(securedErrorHandler)
    val securedAction = new DefaultSecuredAction(securedRequestHandler)
    securedAction(identityService, authenticatorService, Seq())
  }

}
