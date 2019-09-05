package jp.smartreach.members.backend.core.infrastructure.models

import jp.smartreach.members.backend.core.infrastructure.identifiers.{QuestionId, SurveyId}

case class SurveyQuestions (
                             id: QuestionId,
                             surveyId: SurveyId,
                             answer: String
                           )
