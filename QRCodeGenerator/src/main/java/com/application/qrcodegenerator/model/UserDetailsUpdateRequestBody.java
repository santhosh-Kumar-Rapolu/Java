package com.application.qrcodegenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDetailsUpdateRequestBody {

		private String companyName;
		private String companyAddress;
		private String companyContactNumber;
		private String companyWebsite;
        private Long qrCodeId;
        private Long userId;

}
