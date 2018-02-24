package io.github.cactacea.core.infrastructure.models

import io.github.cactacea.core.infrastructure.identifiers.{GroupId, GroupReportId, AccountId}

case class GroupReports(id: GroupReportId,
                        groupId: GroupId,
                        by: AccountId,
                        reportType: Long
                        )
