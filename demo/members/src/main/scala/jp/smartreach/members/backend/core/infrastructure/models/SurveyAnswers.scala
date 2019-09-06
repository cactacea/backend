package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.{MemberId, QuestionId, SurveyId}

case class SurveyAnswers (
                           surveyId: SurveyId,
                           questionId: QuestionId,
                           memberId: MemberId,
                           checked: Boolean
                         )
