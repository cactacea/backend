package io.github.cactacea.backend

import io.github.cactacea.finachat.{ChatService => BaseChatService}

class ChatService extends BaseChatService[ChatAuthInfo](new ChatHandler())