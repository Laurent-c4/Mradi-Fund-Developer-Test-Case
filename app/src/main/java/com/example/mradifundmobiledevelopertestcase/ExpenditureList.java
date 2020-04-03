package com.example.mradifundmobiledevelopertestcase;

import java.util.List;

public class ExpenditureList {
    private List<Expenditure> expenditureList;
    private String statementName;

    public ExpenditureList() {

    }

    public ExpenditureList(List<Expenditure> expenditureList,String statementName){
        this.expenditureList = expenditureList;
        this.statementName =statementName;
    }

    public List<Expenditure> getExpenditureList() {
        return expenditureList;
    }

    public void setExpenditureList(List<Expenditure> expenditureList) {
        this.expenditureList = expenditureList;
    }

    public String getStatementName() {
        return statementName;
    }

    public void setStatementName(String statementName) {
        this.statementName = statementName;
    }
}
