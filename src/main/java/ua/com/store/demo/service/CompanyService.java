package ua.com.store.demo.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.zankowski.iextrading4j.api.exception.IEXTradingException;
import pl.zankowski.iextrading4j.api.stocks.Logo;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.LogoRequestBuilder;
import ua.com.store.demo.config.IextradingConfig;
import ua.com.store.demo.model.CompanyEntity;
import ua.com.store.demo.model.CompanyJson;
import ua.com.store.demo.repo.CompanyRepo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private static final String GET_ALL_COMPANIES_URL = "https://api.iextrading.com/1.0/ref-data/symbols?filter=symbol,name";

    @Value("${limit}")
    private Long limit;

    private final IextradingConfig iextradingConfig;
    private final CompanyRepo companyRepo;

    @Scheduled(cron = "${scheduled.seconds}")
    public void saveCompany() {
        IEXCloudClient cloudClient = iextradingConfig.getConnection();
        var offset = companyRepo.count();
        List<CompanyJson> list = getAllCompanySymbols();
        list.stream().skip(offset * limit).limit(limit).map(c -> convertIntoCompanyEntity(cloudClient, c)).filter(Objects::nonNull).forEach(companyRepo::save);
    }

    private List<CompanyJson> getAllCompanySymbols() {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(GET_ALL_COMPANIES_URL);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    content.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CompanyJson[] companyEntities = new Gson().fromJson(content.toString(), CompanyJson[].class);
        return List.of(companyEntities);
    }

    private CompanyEntity convertIntoCompanyEntity(IEXCloudClient cloudClient, CompanyJson c) {
        try {
            Logo logo = cloudClient.executeRequest(new LogoRequestBuilder()
                    .withSymbol(c.getSymbol())
                    .build());

            return CompanyEntity.builder()
                    .companyName(c.getName())
                    .companySymbol(c.getSymbol())
                    .logoUrl(logo.getUrl())
                    .build();
        } catch (IEXTradingException ex) {
            return null;
        }
    }
}
//
//-Dspring.datasource.username=root
//        -Dspring.datasource.password=zenbook3107
//        -Diextrading.token=Tpk_8a95e8d375bf4926814d6a37f3b13dbb
//        -Diextrading.secretToken=Tsk_bac69ff43a714316832340b8b59c780b
//        -Dserver.port=8080
//        -Dscheduled.seconds="*/10 * * * * *"
//        -Dlimit=20
