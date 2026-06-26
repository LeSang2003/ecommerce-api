package com.demo.service;

import com.demo.model.Color;
import com.demo.repository.ColorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorService {
  private final ColorRepository colorRepository;

  public ColorService(ColorRepository colorRepository){
    this.colorRepository = colorRepository;
  }

  public List<Color> getAllColors(){
    return colorRepository.findAll();
  }

  public Color createColor(Color color){
    return colorRepository.save(color);
  }

  public Color updateColor(Long id, Color newColor){
      Color color = colorRepository.findById(id).orElseThrow();
      color.setName(newColor.getName());
      return colorRepository.save(color);
  }

  public void deleteColor(Long id){
    colorRepository.deleteById(id);
  }

}
