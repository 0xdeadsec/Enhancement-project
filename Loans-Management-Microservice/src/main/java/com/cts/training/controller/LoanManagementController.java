package com.cts.training.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.training.exception.CustomerLoanNotFoundException;
import com.cts.training.exception.LoanNotFoundException;
import com.cts.training.feign.AuthorisationClient;
import com.cts.training.model.CustomerLoan;
import com.cts.training.model.LoanApplication;
import com.cts.training.pojo.CashDeposit;
import com.cts.training.pojo.RealEstate;
import com.cts.training.service.LoanManagementService;

/**
 * This Class is marked as Rest Controller LoanManagementController
 * 
 * @ResquestMapping /loan-management
 */
@RestController
@RequestMapping(value = "/loan-management")
public class LoanManagementController {

	@Autowired
	private LoanManagementService loanService;

	@Autowired
	private AuthorisationClient authClient;

	/**
	 * 
	 * @URL: http://localhost:8100/loan/loan-management/health-check
	 */
	@GetMapping("/health-check")
	public ResponseEntity<String> healthCheck() {
		return new ResponseEntity<>("Ok", HttpStatus.OK);
	}

	/**
	 * getLoanDetails based on LoanId and CustomerId
	 * 
	 * @param token
	 * @param loanId
	 * @param customerId
	 * @return
	 * @throws CustomerLoanNotFoundException
	 */
	@GetMapping(value = "/getLoanDetails/{loanId}/{customerId}")
	public CustomerLoan getLoanDetails(@RequestHeader(name = "Authorization") String token, @PathVariable int loanId,
			@PathVariable int customerId) throws CustomerLoanNotFoundException {
		System.out.println("===============inside getloandetails================");
		if (authClient.validate(token)) {
			return loanService.getLoanDetails(loanId, customerId);
		} else {
			throw new CustomerLoanNotFoundException(
					"Customer Loan Found With LoanId:" + loanId + " But Not Found With CustomerId:" + customerId);
		}
	}

	/**
	 * This method saveRealEstateCollateral based on realEstate Object as parameter
	 * 
	 * @param token
	 * @param realEstate
	 * @return
	 * @throws CustomerLoanNotFoundException
	 * @throws LoanNotFoundException
	 */
	@PostMapping(value = "/saveRealEstateCollateral")
	public ResponseEntity<String> saveRealEsateCollateral(@RequestHeader(name = "Authorization") String token,
			@RequestBody RealEstate realEstate) throws CustomerLoanNotFoundException, LoanNotFoundException {
		if (authClient.validate(token)) {
			return loanService.saveRealEstate(token, realEstate);
		} else {
			return new ResponseEntity<>("Invalid token", HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * This method saveCashDepositeCollateral based on cashEstate Object as
	 * parameter
	 * 
	 * @param token
	 * @param cashDeposit
	 * @return
	 * @throws CustomerLoanNotFoundException
	 * @throws LoanNotFoundException
	 */
	@PostMapping(value = "/saveCashDepositCollateral")
	public ResponseEntity<String> saveCashDepositCollateral(@RequestHeader(name = "Authorization") String token,
			@RequestBody CashDeposit cashDeposit) throws CustomerLoanNotFoundException, LoanNotFoundException {
		if (authClient.validate(token)) {
			System.out.println("Inside loan-management loanId : " + cashDeposit.getLoanId());
			return loanService.saveCashDeposit(token, cashDeposit);
		} else {
			return new ResponseEntity<>("Invalid token", HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * @param loanApplication
	 * @URL: http://localhost:8100/loan/loan-management/applyLoan
	 * @return
	 */
	@PostMapping(value = "/applyLoan")
	public ResponseEntity<String> applyLoan(@RequestBody LoanApplication loanApplication) {
		// System.out.println("inside loan-management-controller"+loanApplication);
		return loanService.applyLoan(loanApplication);
	}

	/**
	 * @param custId
	 * @URL: http://localhost:8100/loan/loan-management/getLoanApplicationStatus
	 * @return
	 */
	@GetMapping(value = "/getLoanApplicationStatus")
	public ArrayList<LoanApplication> viewLoanCust(@RequestHeader int custId) {
		return loanService.viewCustLoan(custId);
	}

	/**
	 
	 * @URL: http://localhost:8100/loan/loan-management/getAll
	 * @return
	 */
	@GetMapping(value = "/getAll")
	public ArrayList<LoanApplication> getAllApplications() {
		return loanService.getAll();
	}

	/**
	 * 
	 * @param applicationId
	 * @URL: http://localhost:8100/loan/loan-management/approveLoanApplication/1
	 * @return
	 */
	@PutMapping(value = "/approveLoanApplication/{applicationId}")
	public ResponseEntity<String> approveLoan(@RequestHeader Integer applicationId) {
		return loanService.approveLoan(applicationId);
	}

	/**
	 
	 * @param applicationId
	 * @URL: http://localhost:8100/loan/loan-management/rejectLoanApplication/1
	 * @return
	 */
	@PutMapping(value = "/rejectLoanApplication/{applicationId}")
	public ResponseEntity<String> rejectLoan(@PathVariable Integer applicationId) {
		return loanService.rejectLoan(applicationId);
	}
}