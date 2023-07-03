package tomato.classifier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tomato.classifier.dto.main.DiseaseDto;
import tomato.classifier.entity.Disease;
import tomato.classifier.repository.main.DiseaseRepository;
import tomato.classifier.test.testDto;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MainService {

    private final DiseaseRepository diseaseRepository;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    @Value("c:/Users/whffu/VScode/forTest/5_dest/Target/")
    private String fileDir;

    @Value("http://127.0.0.1:5000/predict")
    private String url;
    public void saveImg(List<MultipartFile> files) throws IOException {

        if(!files.isEmpty()){
            for(MultipartFile file : files){
                String filePath = fileDir + file.getOriginalFilename();

                file.transferTo(new File(filePath));
            }
        }
    }

    public String predict() throws IOException{
        //간단하게 쓸 수 있는 restTemplate

        String response = restTemplate.getForObject(url, String.class);
        testDto testDto = objectMapper.readValue(response, testDto.class);
        String params = "?name="+ testDto.getName()+"&prob="+testDto.getProb();

        return "/main/result"+params;
    }

    public String predict1() throws IOException{
        //헤더를 만들 수 있는 restTemplate

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[] {MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("",headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        testDto testDto = objectMapper.readValue(response.getBody(), testDto.class);
        String params = "?name="+ testDto.getName()+"&prob="+testDto.getProb();

        return "/main/result"+params;

    }

    public DiseaseDto getDiseaseInfo(Map<String, Object> result){

        String name = (String) result.get("name");
        Integer prob = (int) Math.round(Double.parseDouble((String) result.get("prob")));

        Disease target = diseaseRepository.findById((name))
                .orElseThrow(()-> new IllegalArgumentException("질병 조회 실패"));

        return DiseaseDto.convertDto(target, prob);
    }

}
