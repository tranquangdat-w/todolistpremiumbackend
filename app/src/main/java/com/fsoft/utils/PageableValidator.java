package com.fsoft.utils;

import com.fsoft.exceptions.ApiException;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

public class PageableValidator {

  private PageableValidator() {}

  public static void validate(Pageable pageable, Class<?> entityClass) {
    if (pageable.getPageNumber() < 0) {
      throw new ApiException("Page number must not be less than zero!", HttpStatus.BAD_REQUEST.value());
    }

    if (pageable.getPageSize() < 1) {
      throw new ApiException("Page size must not be less than one!", HttpStatus.BAD_REQUEST.value());
    }

    validateSort(pageable.getSort(), entityClass);
  }

  private static void validateSort(Sort sort, Class<?> entityClass) {
    if (sort.isSorted()) {
      List<String> validFields = Arrays.stream(entityClass.getDeclaredFields())
          .map(field -> field.getName())
          .toList();

      for (Sort.Order order : sort) {
        if (!validFields.contains(order.getProperty())) {
          throw new ApiException("Invalid sort property: " + order.getProperty(), HttpStatus.BAD_REQUEST.value());
        }
      }
    }
  }
}
