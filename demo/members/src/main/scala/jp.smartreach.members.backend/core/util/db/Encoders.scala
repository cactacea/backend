package jp.smartreach.members.backend.core.util.db

import io.getquill.context.sql.SqlContext
import io.smartreach.members.backend.core.domain.enums._
import jp.smartreach.members.backend.core.infrastructure.identifiers._

trait Encoders {
  this: SqlContext[_, _] =>

  implicit val adjustmentIdDecode: MappedEncoding[AdjustmentId, Long] = MappedEncoding[AdjustmentId, Long] (id => id.onlyValue)
  implicit val adjustmentIdEncode: MappedEncoding[Long, AdjustmentId] = MappedEncoding[Long, AdjustmentId] (long => AdjustmentId(long))
  implicit val adjustmentTimeIdDecode: MappedEncoding[AdjustmentTimeId, Long] = MappedEncoding[AdjustmentTimeId, Long] (id => id.onlyValue)
  implicit val adjustmentTimeIdEncode: MappedEncoding[Long, AdjustmentTimeId] = MappedEncoding[Long, AdjustmentTimeId] (long => AdjustmentTimeId(long))
  implicit val cardExchangeIdDecode: MappedEncoding[CardExchangeId, Long] = MappedEncoding[CardExchangeId, Long] (id => id.onlyValue)
  implicit val cardExchangeIdEncode: MappedEncoding[Long, CardExchangeId] = MappedEncoding[Long, CardExchangeId] (long => CardExchangeId(long))
  implicit val cardIdDecode: MappedEncoding[CardId, Long] = MappedEncoding[CardId, Long] (id => id.onlyValue)
  implicit val cardIdEncode: MappedEncoding[Long, CardId] = MappedEncoding[Long, CardId] (long => CardId(long))
  implicit val eventIdDecode: MappedEncoding[EventId, Long] = MappedEncoding[EventId, Long] (id => id.onlyValue)
  implicit val eventIdEncode: MappedEncoding[Long, EventId] = MappedEncoding[Long, EventId] (long => EventId(long))
  implicit val groupIdDecode: MappedEncoding[GroupId, Long] = MappedEncoding[GroupId, Long] (id => id.onlyValue)
  implicit val groupIdEncode: MappedEncoding[Long, GroupId] = MappedEncoding[Long, GroupId] (long => GroupId(long))
  implicit val memberIdDecode: MappedEncoding[MemberId, Long] = MappedEncoding[MemberId, Long] (id => id.onlyValue)
  implicit val memberIdEncode: MappedEncoding[Long, MemberId] = MappedEncoding[Long, MemberId] (long => MemberId(long))
  implicit val positionIdDecode: MappedEncoding[PositionId, Long] = MappedEncoding[PositionId, Long] (id => id.onlyValue)
  implicit val positionIdEncode: MappedEncoding[Long, PositionId] = MappedEncoding[Long, PositionId] (long => PositionId(long))
  implicit val questionIdDecode: MappedEncoding[QuestionId, Long] = MappedEncoding[QuestionId, Long] (id => id.onlyValue)
  implicit val questionIdEncode: MappedEncoding[Long, QuestionId] = MappedEncoding[Long, QuestionId] (long => QuestionId(long))
  implicit val scheduleIdDecode: MappedEncoding[ScheduleId, Long] = MappedEncoding[ScheduleId, Long] (id => id.onlyValue)
  implicit val scheduleIdEncode: MappedEncoding[Long, ScheduleId] = MappedEncoding[Long, ScheduleId] (long => ScheduleId(long))
  implicit val surveyIdDecode: MappedEncoding[SurveyId, Long] = MappedEncoding[SurveyId, Long] (id => id.onlyValue)
  implicit val surveyIdEncode: MappedEncoding[Long, SurveyId] = MappedEncoding[Long, SurveyId] (long => SurveyId(long))
  implicit val taskIdDecode: MappedEncoding[TaskId, Long] = MappedEncoding[TaskId, Long] (id => id.onlyValue)
  implicit val taskIdEncode: MappedEncoding[Long, TaskId] = MappedEncoding[Long, TaskId] (long => TaskId(long))
  implicit val taskItemIdDecode: MappedEncoding[TaskItemId, Long] = MappedEncoding[TaskItemId, Long] (id => id.onlyValue)
  implicit val taskItemIdEncode: MappedEncoding[Long, TaskItemId] = MappedEncoding[Long, TaskItemId] (long => TaskItemId(long))


  implicit val adjustmentAnswerTypeDecode: MappedEncoding[AdjustmentAnswerType, Byte]
  = MappedEncoding[AdjustmentAnswerType, Byte] (enumValue => enumValue.toValue)
  implicit val adjustmentAnswerTypeEncode: MappedEncoding[Byte, AdjustmentAnswerType]
  = MappedEncoding[Byte, AdjustmentAnswerType] (long => AdjustmentAnswerType.forName(long))
  implicit val communicationTypeDecode: MappedEncoding[CommunicationType, Byte]
  = MappedEncoding[CommunicationType, Byte] (enumValue => enumValue.toValue)
  implicit val communicationTypeEncode: MappedEncoding[Byte, CommunicationType]
  = MappedEncoding[Byte, CommunicationType] (long => CommunicationType.forName(long))
  implicit val eventJoinTypeDecode: MappedEncoding[EventJoinType, Byte]
  = MappedEncoding[EventJoinType, Byte] (enumValue => enumValue.toValue)
  implicit val eventJoinTypeEncode: MappedEncoding[Byte, EventJoinType]
  = MappedEncoding[Byte, EventJoinType] (long => EventJoinType.forName(long))
  implicit val groupAuthorityTypeDecode: MappedEncoding[GroupAuthorityType, Byte]
  = MappedEncoding[GroupAuthorityType, Byte] (enumValue => enumValue.toValue)
  implicit val groupAuthorityTypeEncode: MappedEncoding[Byte, GroupAuthorityType]
  = MappedEncoding[Byte, GroupAuthorityType] (long => GroupAuthorityType.forName(long))
  implicit val surveyTypeDecode: MappedEncoding[SurveyType, Byte]
  = MappedEncoding[SurveyType, Byte] (enumValue => enumValue.toValue)
  implicit val surveyTypeEncode: MappedEncoding[Byte, SurveyType]
  = MappedEncoding[Byte, SurveyType] (long => SurveyType.forName(long))
  implicit val taskStatusTypeDecode: MappedEncoding[TaskStatusType, Byte]
  = MappedEncoding[TaskStatusType, Byte] (enumValue => enumValue.toValue)
  implicit val taskStatusTypeEncode: MappedEncoding[Byte, TaskStatusType]
  = MappedEncoding[Byte, TaskStatusType] (long => TaskStatusType.forName(long))


}

