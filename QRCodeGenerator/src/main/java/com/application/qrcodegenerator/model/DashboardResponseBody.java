package com.application.qrcodegenerator.model;

import java.util.List;
import java.util.Optional;

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
public class DashboardResponseBody {
	
	private Optional<User> user;
	private List<QrCodeDetails> qrCodeDetails;

}
