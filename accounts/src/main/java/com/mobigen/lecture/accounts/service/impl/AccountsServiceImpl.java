package com.mobigen.lecture.accounts.service.impl;

import java.util.Optional;
import java.util.Random;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import com.mobigen.lecture.accounts.constants.AccountsConstants;
import com.mobigen.lecture.accounts.dto.AccountsDto;
import com.mobigen.lecture.accounts.dto.AccountsMsgDto;
import com.mobigen.lecture.accounts.dto.CustomerDto;
import com.mobigen.lecture.accounts.entity.Accounts;
import com.mobigen.lecture.accounts.entity.Customer;
import com.mobigen.lecture.accounts.exception.CustomerAlreadyExistsException;
import com.mobigen.lecture.accounts.mapper.AccountsMapper;
import com.mobigen.lecture.accounts.mapper.CustomerMapper;
import com.mobigen.lecture.accounts.repository.AccountsRepository;
import com.mobigen.lecture.accounts.repository.CustomerRepository;
import com.mobigen.lecture.accounts.service.IAccountsService;
import com.mobigen.lecture.common.exception.ResourceNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    // @AllArgsConstructor + private = @Autowired, spring 의 의존성 주입
    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private final StreamBridge streamBridge;

    /**
     * @param customerDto - CustomerDto Object
     */
    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if(optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobileNumber "
                    +customerDto.getMobileNumber());
        }
        Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }

    @Override
    public boolean sendCommunication(String param) {
        AccountsMsgDto dto = new AccountsMsgDto(1L, "create-" + param, "create-" + param, "000-000-" + param);
        log.info("Sending Communication request for the details: {}", dto.toString());
        boolean result = streamBridge.send("sendCommunication-out-0", dto);
        log.info("Is the Communication request successfully triggered ? : {}", result);
        return result;
    }

    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Accounts Details based on a given mobileNumber
     */
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        return customerDto;
    }

    /**
     * @param customerDto - CustomerDto Object
     * @return boolean indicating if the update of Account details is successful or not
     */
    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto !=null ){
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "AccountNumber", accountsDto.getAccountNumber().toString())
            );
            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString())
            );
            CustomerMapper.mapToCustomer(customerDto,customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return  isUpdated;
    }

    /**
     * @param mobileNumber - Input Mobile Number
     * @return boolean indicating if the delete of Account details is successful or not
     */
    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    /**
     * @param accountNumber - Long
     * @return boolean indicating if the update of communication status is successful or not
     */
    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated = false;
        if(accountNumber != null){
            // Accounts accounts = accountsRepository.findById(accountNumber).orElseThrow(
            //         () -> new ResourceNotFoundException("Account", "AccountNumber", accountNumber.toString())
            // );
            // accounts.setCommunicationSw(true);
            // accountsRepository.save(accounts);
            isUpdated = true;
        }
        log.info(">>> Update Communication Status update [" + accountNumber + "] isUpdate ? " + isUpdated);
        return  isUpdated;
    }
}
