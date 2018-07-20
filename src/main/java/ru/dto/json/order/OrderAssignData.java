package ru.dto.json.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderAssignData {
    private List<Integer> assignedCompanies;
    private Float dispatcherPrice;
}
