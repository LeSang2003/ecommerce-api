package com.demo.service;

import com.demo.model.Size;
import com.demo.repository.SizeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SizeService {
  private final SizeRepository sizeRepository;

  public SizeService(SizeRepository sizeRepository){
    this.sizeRepository = sizeRepository;
  }
  
  public List<Size> getAllSizes(){
    return sizeRepository.findAll();
  }

  public Size save(Size size){
    return sizeRepository.save(size);
  }

  public void deleteSize(Long id){
    sizeRepository.deleteById(id);
  }

}
