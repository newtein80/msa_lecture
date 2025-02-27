package com.mobigen.lecture.accounts.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mobigen.lecture.accounts.dto.AccountsDto;
import com.mobigen.lecture.accounts.dto.CardsDto;
import com.mobigen.lecture.accounts.dto.CustomerDetailsDto;
import com.mobigen.lecture.accounts.dto.LoansDto;
import com.mobigen.lecture.accounts.entity.Accounts;
import com.mobigen.lecture.accounts.entity.Customer;
import com.mobigen.lecture.accounts.mapper.AccountsMapper;
import com.mobigen.lecture.accounts.mapper.CustomerMapper;
import com.mobigen.lecture.accounts.repository.AccountsRepository;
import com.mobigen.lecture.accounts.repository.CustomerRepository;
import com.mobigen.lecture.accounts.service.ICustomersService;
import com.mobigen.lecture.accounts.service.client.CardsFeignClient;
import com.mobigen.lecture.accounts.service.client.LoansFeignClient;
import com.mobigen.lecture.common.exception.ResourceNotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     *  @param correlationId - Correlation ID value generated at Edge server
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;

    }
}
