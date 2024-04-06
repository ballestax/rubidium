/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */
package com.rb;

/**
 *
 * @author ballestax
 */
public class Filter {

    public static final String FILTER_TEXT_EQUALS = "=";
    public static final String FILTER_TEXT_START = "_";
    public static final String FILTER_TEXT_CONTAINS = "_";
    public static final String FILTER_NUM_EQUALS = "=";
    public static final String FILTER_NUM_GREATER = ">";
    public static final String FILTER_NUM_GREATER_EQUAL = ">=";
    public static final String FILTER_NUM_LESS = "<";
    public static final String FILTER_NUM_LESS_EQUAL = "<=";
    public static final String FILTER_NUM_DIFFERENT = "<>";
    public static final String FILTER_BOOL_EQUAL = "=";
    public static final String FILTER_BOOL_DIFFERENT = "!=";

    public static final String[] FILTROS_TEXTO = {"IGUAL A", "EMPIEZA POR", "CONTIENE"};
    public static final String[] FILTROS_NUMERO = {"=", ">", ">=", "<", "<=", "<>"};
    public static final String[] FILTROS_BOOLEANOS = {"=", "<>"};

    public static final String[] OPERADORES = {"Y", "O"};

    private Field field;
    private String value;
    private boolean isFirst;
    private String operator;
    private String condition;
    private String conditionTitle;
    private String query;

    public Filter() {
    }

    public Filter(Field field, String value, String condition) {
        this.field = field;
        this.value = value;
        this.condition = condition;
        makeQuery();
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
        makeQuery();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        makeQuery();
    }

    public boolean isIsFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
        makeQuery();
    }

    public String getCondition() {
        return condition;
    }

    public String getConditionTitle() {
        return conditionTitle;
    }

    public void setConditionTitle(String conditionTitle) {
        this.conditionTitle = conditionTitle;
    }

    public void setCondition(String condition) {
        this.condition = condition;
        makeQuery();
    }

    private void makeQuery() {
        boolean isText = getField().getType() == Field.T_TEXT;
        StringBuilder strQuery = new StringBuilder();
        String[] OPERATOR = {"AND", "OR"};
        strQuery.append((getOperator() == null || getOperator().isEmpty()) ? ""
                : getOperator().equals("Y") ? OPERATOR[0] : OPERATOR[1]).append(" ");
        strQuery.append(getField().getNameInDB()).append(" ");
        if (isText) {
            strQuery.append(getCondition().replace("<>", getValue()));
        } else {
            strQuery.append(getCondition()).append(" ");
            strQuery.append(getValue());
        }
        query = strQuery.toString();
    }

    public String getQuery() {
        return query;
    }

}
