package net.okhotnikov.harald.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.okhotnikov.harald.config.MapperConfig;
import net.okhotnikov.harald.model.web.Error;
import net.okhotnikov.harald.model.web.HttpCall;
import net.okhotnikov.harald.model.web.HttpMethod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class RestService {

    public static RestService instance = new RestService(MapperConfig.mapper);

    private final ObjectMapper mapper;

    private RestService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <S,R> void send(HttpCall<S,R> call){
        AsyncService.instance.execute(()->{sendCurrentThread(call);});
    }

    public <S,R> void sendCurrentThread(HttpCall<S,R> call){
        try {
            URL url=new URL(call.endpoint.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            HttpMethod method = call.method;
            connection.setRequestMethod(method.name());
            connection.setReadTimeout(150000);
            connection.setConnectTimeout(150000);

            for(Map.Entry<String,String> entry: call.headers.entrySet())
                connection.setRequestProperty (entry.getKey(), entry.getValue());

            connection.setDoInput(method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE || method == HttpMethod.PATCH);
            connection.setDoOutput(true);
    //        System.out.println(msg);

            String msg = mapper.writeValueAsString(call.toSend);

            if (method!= HttpMethod.GET) {
                try(OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
                    writer.write(msg);
                    writer.flush();
                }
            }

            int respCode = connection.getResponseCode();

            boolean success = respCode == 200;

            InputStream inputStream = success ? connection.getInputStream() : connection.getErrorStream();

            StringBuilder res = new StringBuilder();
            String str="";
            try(BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream))) {
                while ((str = reader.readLine()) != null) {
                    res.append(str);
                }
            }
            str = res.toString();
            if(success){
                call
                    .callBack
                    .action(str.isEmpty() ? null :
                            mapper
                            .readValue(res.toString()
                                    , new TypeReference<R>() {}));
            } else{
                call
                    .error
                    .action(
                        Error.of(respCode,str.isEmpty() ? null : str)
                    );
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            Error error = Error.CodeException;
            error.message = e.getClass().getName();
            call.error.action(error);
        }
    }
}
