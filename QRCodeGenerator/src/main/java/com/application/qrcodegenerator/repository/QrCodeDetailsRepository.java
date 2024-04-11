package com.application.qrcodegenerator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.qrcodegenerator.model.QrCodeDetails;

public interface QrCodeDetailsRepository extends JpaRepository<QrCodeDetails, Long> {

	List<QrCodeDetails> findAllByUserId(Long id);

}
