package com.application.qrcodegenerator.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.qrcodegenerator.model.QrCodeDetails;
import com.application.qrcodegenerator.model.ResultDTO;
import com.application.qrcodegenerator.model.User;
import com.application.qrcodegenerator.model.UserDetailsUpdateRequestBody;
import com.application.qrcodegenerator.model.UserJson;
import com.application.qrcodegenerator.repository.QrCodeDetailsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QRService {

	@Autowired
	private QrCodeDetailsRepository qrCodeDetailsRepository;

	public ResultDTO processQR(User user) {
		ResultDTO resultDTO = new ResultDTO();
		QrCodeDetails qrCodeDetail = new QrCodeDetails();
		// creating QR code using company details
		ObjectMapper objectMapper = new ObjectMapper();
		String qrcodeJson;
		try {
			qrcodeJson = objectMapper.writeValueAsString(userToJson(user));

			// QR code to model class
			resultDTO.setQrValue(qrcodeJson);

			// Saving QR code in the database
			qrCodeDetail.setQrCode(qrcodeJson);
			qrCodeDetail.setUserId(user.getId());
			qrCodeDetailsRepository.save(qrCodeDetail);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultDTO;
	}

	public List<QrCodeDetails> getQrCodes(Long id) {
		return qrCodeDetailsRepository.findAllByUserId(id);
	}

	private static UserJson userToJson(User user) {
		UserJson userJson = new UserJson();
		userJson.setCompanyName(user.getCompanyName());
		userJson.setCompanyAddress(user.getCompanyAddress());
		userJson.setCompanyContactNumber(user.getCompanyContactNumber());
		userJson.setCompanyWebsite(user.getCompanyWebsite());
		return userJson;
	}
	
	public QrCodeDetails saveQrcode(QrCodeDetails qrCodeDetails) {
		return qrCodeDetailsRepository.save(qrCodeDetails);
	}
	
	public boolean isAlreadyQrCodeExistsForUser(QrCodeDetails requestBody) {
		return qrCodeDetailsRepository.existsByQrCodeAndUserId(requestBody.getQrCode(), requestBody.getUserId());
	}
	
	public boolean deleteqrcode(Long id) {
		 try {
		        qrCodeDetailsRepository.deleteById(id);
		        return true; // Deletion successful
		    }catch (Exception e) {

        return false; // Deletion failed
    }
	}
	
	public QrCodeDetails updateQRcode(UserDetailsUpdateRequestBody details) {
		String qrcodeJson;
		ObjectMapper objectMapperqr = new ObjectMapper();
		try {
		QrCodeDetails existingqrcode = qrCodeDetailsRepository.findById(details.getQrCodeId())
                .orElseThrow(() -> new IllegalArgumentException("user not found with id: " + details.getQrCodeId()));
		UserJson newDetails = new UserJson();
		newDetails.setCompanyName(details.getCompanyName());
		newDetails.setCompanyAddress(details.getCompanyAddress());
		newDetails.setCompanyContactNumber(details.getCompanyContactNumber());
		newDetails.setCompanyWebsite(details.getCompanyWebsite());
		
			qrcodeJson = objectMapperqr.writeValueAsString(newDetails);
			existingqrcode.setQrCode(qrcodeJson);
			return qrCodeDetailsRepository.save(existingqrcode);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
}
