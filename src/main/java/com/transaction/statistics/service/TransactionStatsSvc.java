package com.transaction.statistics.service;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.transaction.statistics.dto.TxnStatsDto;
import com.transaction.statistics.dto.TransactionDto;
import com.transaction.statistics.exceptions.InvalidTxnException;

@Service
public class TransactionStatsSvc {

	@Value("${statsBasedOnSeconds}")
	private long statsBasedOnSeconds;
	private static final List<TransactionDto> TRANSACTION_LIST = new ArrayList<>();
	private TxnStatsDto stats;
	private Object lock = new Object();

	public TxnStatsDto getStats() {
		return stats;
	}

	public void addTransacation(TransactionDto transaction) {
		long test = System.currentTimeMillis();
		if ((System.currentTimeMillis() - transaction.getTimeStamp()) > statsBasedOnSeconds) {
			throw new InvalidTxnException();
		}

		synchronized (lock) {
			TRANSACTION_LIST.add(transaction);
			calculateStats();
		}

	}

	@Scheduled(fixedRate = 60000, initialDelay = 5000)
	public void clearOld() {
		synchronized (lock) {
			TRANSACTION_LIST.removeIf(
					transcation -> (System.currentTimeMillis() - transcation.getTimeStamp()) > statsBasedOnSeconds);
			calculateStats();
		}
	}

	@Async
	public void calculateStats() {
		DoubleSummaryStatistics stat = TRANSACTION_LIST.stream().mapToDouble(TransactionDto::getAmount)
				.summaryStatistics();
		stats = new TxnStatsDto(stat.getSum(), stat.getAverage(), stat.getMax(), stat.getMin(), stat.getCount());
	}

}
