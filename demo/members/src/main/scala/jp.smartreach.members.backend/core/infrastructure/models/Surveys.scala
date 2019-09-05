package jp.smartreach.members.backend.core.infrastructure.models

import io.smartreach.members.backend.core.domain.enums.SurveyType
import jp.smartreach.members.backend.core.infrastructure.identifiers.{GroupId, SurveyId}

case class Surveys (
                     id: SurveyId,
                     title: String,
                     description: String,
                     surveyType: SurveyType,
                     groupId: GroupId,
                     createdAt: Long
                   )
