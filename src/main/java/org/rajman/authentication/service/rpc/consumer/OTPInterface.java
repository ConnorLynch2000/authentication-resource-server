package org.rajman.authentication.service.rpc.consumer;

public interface OTPInterface {

    void generateOTPAndSendSms(String uniquePhrase, String phoneNumber, String text) throws Exception;

    void generateOTPAndSendSms(String uniquePhrase, String phoneNumber, String text, int expirationMinute) throws Exception;


    boolean validateOTP(String uniquePhrase, String code);

    void generateOTPAndSendEmail(String uniquePhrase, String from, String to, String cc, String subject, String html, boolean forIOS) throws Exception;
}
