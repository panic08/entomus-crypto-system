package by.panic.entomus.service.implement;

import by.panic.entomus.payload.CreatePaymentQrRequest;
import by.panic.entomus.service.PaymentService;
import by.panic.entomus.util.QrUtil;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final QrUtil qrUtil;

    @Override
    public ResponseEntity<byte[]> createPaymentQr(String apiKey, CreatePaymentQrRequest createPaymentQrRequest) {
        return null;
    }

//    @Override
//    public byte[] createQr(String apiKey) {
//        File file = null;
//
//        try {
//            file = File.createTempFile("qrc", ".png");
//
//            qrUtil.generateQRCode("", file.getPath(), 300, 300);
//        } catch (IOException | WriterException e) {
//            log.warn(e.getMessage());
//        }
//
//        System.out.println(file.getPath());
//        return new byte[0];
//    }
}
