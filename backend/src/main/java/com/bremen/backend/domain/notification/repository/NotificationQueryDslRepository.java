package com.bremen.backend.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bremen.backend.domain.notification.entity.QNotification;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationQueryDslRepository {
	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;
	@Transactional
	public Long updateColumnForIds(ArrayList<Long> idList,Long userId) {
		QNotification notification = QNotification.notification;

		Long updateCount =  queryFactory
			.update(notification)
			.set(notification.isDeleted, true)
			.set(notification.deleteTime, LocalDateTime.now())
			.where(notification.user.id.eq(userId).and(notification.id.in(idList)))
			.execute();

		entityManager.flush();
		entityManager.clear();

		return updateCount;

	}
}
