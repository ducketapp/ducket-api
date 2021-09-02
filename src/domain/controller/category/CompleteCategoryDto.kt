package io.budgery.api.domain.controller.category

import com.fasterxml.jackson.annotation.JsonUnwrapped
import domain.model.category.Category

class CompleteCategoryDto(@JsonUnwrapped val category: Category)