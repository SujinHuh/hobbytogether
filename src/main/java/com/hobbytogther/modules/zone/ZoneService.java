package com.hobbytogther.modules.zone;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    /** data를 넣을 수 있는 지점이 여러 군데가 있다. */
    //화이트 리스트 사용
    @PostConstruct /** Bean이 만들어진 이후에 실행이되는 지점 */
    public void initZoneData() throws IOException {
        if(zoneRepository.count() == 0) {
            File f = new File("/Users/huhsujin/IdeaProjects/zones_kr.csv");
            /**zones_kr.csv있는 데이터를 객체로 읽어옴 */
            /** 파일에 리드 올라인 첫번째인자 (spring이 지원해주는 resorce추상화에서 경로를 가져올 수 있음) */
            List<Zone> zoneList = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8).stream()
                    /** 각각 라인을 한 줄씩 읽어온다. / 한 줄을 Jon이라는 Entity객체로 변화 */
                    .map(line -> {
                        /**라인을 쪼갠다. 배열이 나온다. */
                        String[] split = line.split(",");
                        return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
                        /**zone에 한줄씩 객체가 생기고 콜랙트해서, 마지막에 리스트로 받는다. */
                    }).collect(Collectors.toList());

            zoneRepository.saveAll(zoneList);
        }
    }

}

