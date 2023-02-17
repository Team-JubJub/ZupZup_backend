package com.rest.api.store.service;

import domain.store.Store;
import dto.store.customer.response.StoreResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.StoreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class StoreService {

    @Autowired
    ModelMapper modelMapper;
    private final StoreRepository storeRepository;

    // <-------------------- GET part -------------------->
    public List<StoreResponseDto.GetStoreDto> storeList() {   // 현재는 예외처리할 것 없어 보임
        List<Store> allStoreListEntity = storeRepository.findAll(); // 나중에는 위치기반 등으로 거르게 될 듯?
        List<StoreResponseDto.GetStoreDto> allStoreListDto = allStoreListEntity.stream()
                .map(m -> modelMapper.map(m, StoreResponseDto.GetStoreDto.class))
                .collect(Collectors.toList());

        return allStoreListDto;
    }



}
