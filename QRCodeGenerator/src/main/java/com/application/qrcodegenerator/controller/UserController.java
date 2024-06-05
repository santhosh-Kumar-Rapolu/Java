package com.application.qrcodegenerator.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.qrcodegenerator.model.Base64Input;
import com.application.qrcodegenerator.model.DashboardRequestBody;
import com.application.qrcodegenerator.model.DashboardResponseBody;
import com.application.qrcodegenerator.model.QrCodeDetails;
import com.application.qrcodegenerator.model.ResponseBody;
import com.application.qrcodegenerator.model.ResultDTO;
import com.application.qrcodegenerator.model.User;
import com.application.qrcodegenerator.model.UserDetailsUpdateRequestBody;
import com.application.qrcodegenerator.model.UserRegistrationResponse;
import com.application.qrcodegenerator.service.QRService;
import com.application.qrcodegenerator.service.UserService;
import com.application.qrcodegenerator.service.Utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private QRService qrService;

	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserRegistrationResponse createUser(@RequestBody User user) {
		UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
		String qrCode = null;
		try {
			if (!userService.isUserAlreadyexists(user)) {
				User savedUser = userService.createUser(user);
				// check user saved in database
				if (savedUser.getId() != null) {
					// if user saved with company generate QR code
					if (savedUser.getRoleId() == 1) {
						qrCode = processQR(savedUser);
					}
					userRegistrationResponse.setQrCode(qrCode != null ? qrCode : null);
					userRegistrationResponse.setMessage(Utility.registerSuccessMsg);
					userRegistrationResponse.setError(null);
				} else {
					userRegistrationResponse.setQrCode(qrCode);
					userRegistrationResponse.setMessage(Utility.registerfailureMsg);
				}
			}else {
				userRegistrationResponse.setQrCode(qrCode);
				userRegistrationResponse.setMessage(Utility.alreadyRegisteredUserMsg);
			}
			return userRegistrationResponse;

		} catch (Exception e) {
			userRegistrationResponse.setQrCode(qrCode);
			userRegistrationResponse.setMessage(Utility.registerfailureMsg);
			userRegistrationResponse.setError(e.getMessage());
			return userRegistrationResponse;
		}
	}

	public String processQR(User user) {
		try {
			ResultDTO result = qrService.processQR(user);
			return result.getQrValue();
		} catch (Exception e) {
			return "Exception found";
		}
	}

	@PostMapping(value = "/getDashboardData", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardResponseBody> getDashboardData(@RequestBody DashboardRequestBody requestBody) {
		Optional<User> user = Optional.ofNullable(new User());
		List<QrCodeDetails> qrlist = new ArrayList<>();
		DashboardResponseBody dashboardResponseBody = new DashboardResponseBody();
		try {
			user = userService.getUserById(requestBody.getUserId());
			qrlist = qrService.getQrCodes(requestBody.getUserId());

			dashboardResponseBody.setUser(user);
			dashboardResponseBody.setQrCodeDetails(qrlist);
			return new ResponseEntity<>(dashboardResponseBody, HttpStatus.OK);
		} catch (Exception e) {
			dashboardResponseBody.setUser(null);
			dashboardResponseBody.setQrCodeDetails(null);
			return new ResponseEntity<>(dashboardResponseBody, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/saveQrCode", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseBody> saveUserQrCode(@RequestBody QrCodeDetails requestBody) {
		ResponseBody responseBody = new ResponseBody();
		try {
			if(!qrService.isAlreadyQrCodeExistsForUser(requestBody)) {
				qrService.saveQrcode(requestBody);
				responseBody.setMessage(Utility.qrCodeSuccessMsg);
				responseBody.setError(null);
			}else {
				responseBody.setMessage(Utility.qrCodeAlreadyexistMsg);
				responseBody.setError(null);
			}
			return new ResponseEntity<>(responseBody, HttpStatus.OK);
		} catch (Exception e) {
			responseBody.setMessage(Utility.qrCodeFailureMsg);
			responseBody.setError(e.getMessage());
			return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/companyDetails")
	public ResponseEntity<DashboardResponseBody> getCompanyDetails(@RequestParam Long id) {
		Optional<User> user = Optional.ofNullable(new User());
		DashboardResponseBody dashboardResponseBody = new DashboardResponseBody();
		try {
			user = userService.getUserById(id);
			dashboardResponseBody.setUser(user);
			dashboardResponseBody.setQrCodeDetails(null);
			return new ResponseEntity<>(dashboardResponseBody, HttpStatus.OK);
		} catch (Exception e) {
			dashboardResponseBody.setUser(null);
			dashboardResponseBody.setQrCodeDetails(null);
			return new ResponseEntity<>(dashboardResponseBody, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/deleteQrCode")
	public ResponseEntity<ResponseBody> delete(@RequestParam Long id) {
		ResponseBody responseBody = new ResponseBody();
		try {
			qrService.deleteqrcode(id);
			responseBody.setMessage(Utility.deleteQrCodeSuccessMsg);
			responseBody.setError(null);
			return new ResponseEntity<>(responseBody, HttpStatus.OK);
		} catch (Exception e) {
			responseBody.setMessage(Utility.deleteQrCodeFailureMsg);
			responseBody.setError(e.getMessage());
			return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/updateCompanyDetails", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseBody> updateCompanyDetails(@RequestBody UserDetailsUpdateRequestBody user) {
		ResponseBody responseBody = new ResponseBody();
		try {
			User updatedUser = userService.updateUser(user);
			QrCodeDetails updatedqrcode = qrService.updateQRcode(user);
			if (updatedUser != null && updatedqrcode != null) {
				responseBody.setMessage(Utility.detailsUpdateSuccessMsg);
				responseBody.setError(null);
				return new ResponseEntity<>(responseBody, HttpStatus.OK);
			}

		} catch (Exception e) {
			responseBody.setMessage(Utility.detailsUpdateFailureMsg);
			responseBody.setError(e.getMessage());
			return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return null;
	}

	@PostMapping(value = "/saveImage", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseBody> savePngFile(@RequestBody Base64Input base64String) throws IOException {
		ResponseBody responseBody = new ResponseBody();
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.WEEK_OF_YEAR);
		int hour = c.get(Calendar.HOUR);
		int min = c.get(Calendar.MINUTE);
		int sec = c.get(Calendar.SECOND);
		String filename = "" + year + hour + min + sec + ".png";
		String filePath1 = "C:\\apache-tomcat-10.1.20/webapps/ROOT/QRCodeImages/" + filename;
		try {

			// Remove the prefix from base64 string
			String base64Image = base64String.getImageData().split(",")[1];

			// Decode base64 string
			byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

			// Write decoded bytes to file
			try (FileOutputStream fos = new FileOutputStream(filePath1)) {
				fos.write(decodedBytes);
			}
			responseBody.setMessage("https://pushkarchessclub.com/QRCodeImages/" + filename);
			responseBody.setError(null);
			return new ResponseEntity<>(responseBody, HttpStatus.OK);
		} catch (IOException e) {
			System.err.println("Error saving file: " + e.getMessage());
			responseBody.setMessage(Utility.saveImgFailureMsg);
			responseBody.setError(e.getMessage());
			return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
