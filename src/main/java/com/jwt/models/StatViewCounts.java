package com.jwt.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jwt.enums.SVC;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

@JsonIgnoreProperties
public class StatViewCounts extends StatsViewModel implements Comparable<StatViewCounts>{

    private Integer count;

    // for each attribute an empty , false or zero value is returned if no value or null is passed in
    public StatViewCounts(Object[] record) {
        this.setStatName( ((String) record[SVC.STAT_NAME]).length() > 0  ? (String) record[SVC.STAT_NAME]  : "");
        this.setSuccess( ((Boolean) record[SVC.SUCCESS]).booleanValue()  ? (Boolean) record[SVC.SUCCESS]   : false);
        this.setHalf( ((Integer)    record[SVC.HALF]).equals(null)       ? (Integer)  record[SVC.HALF]     : 0);

        this.setFirstName( ((String) record[SVC.FIRST_NAME]) == null || !((String) record[SVC.FIRST_NAME]).equals("") ? (String) record[SVC.FIRST_NAME] : "");
        this.setLastName(  ((String) record[SVC.LAST_NAME])  == null || !((String) record[SVC.LAST_NAME]).equals("")  ? (String) record[SVC.LAST_NAME]  : "");
        this.setHomeTeam(  ((String) record[SVC.HOME_TEAM])  == null || !((String) record[SVC.HOME_TEAM]).equals("")  ? (String) record[SVC.HOME_TEAM]  : "");
        this.setAwayTeam(  ((String) record[SVC.AWAY_TEAM])  == null || !((String) record[SVC.AWAY_TEAM]).equals("")  ? (String) record[SVC.AWAY_TEAM]  : "");
        this.setLocation(  ((String) record[SVC.LOCATION])   == null || !((String) record[SVC.LOCATION]).equals("") ? (String) record[SVC.LOCATION]   : "");

        this.setSeason( ((Integer) record[SVC.SEASON]).equals(null)                   ? 0 : (Integer) record[SVC.SEASON]);
        this.setFixtureDate( ((Date) record[SVC.FIXTURE_DATE]).equals(null)           ? new Date(System.currentTimeMillis()) : (Date) record[SVC.FIXTURE_DATE] );
        this.setTime_occurred( ((BigDecimal)  record[SVC.TIME_OCCURRED]).equals(null) ? new BigDecimal(0) : (BigDecimal)  record[SVC.TIME_OCCURRED] );
        this.setCount( (convertToInteger(record[SVC.STAT_COUNT])) == null             ? 0 : convertToInteger(record[SVC.STAT_COUNT]) );
    }

    private Integer convertToInteger(Object o) {
        int count = -1;
        if(o instanceof BigInteger)
            try {
                BigInteger num = (BigInteger)o;
                count = num.intValueExact();
            } catch (ArithmeticException e) {
                System.out.println("Conversion not possible - value is too large for int");
            }
        return count;
    }

    public StatViewCounts(String statName, Integer count) {
        this.setStatName(  statName);
        this.setCount( count );

        // set defaults for all the rest
        this.setSuccess( false);
        this.setHalf( 1);

        this.setFirstName( "" );
        this.setLastName( "" );
        this.setHomeTeam( "" );
        this.setAwayTeam( "" );
        this.setLocation( "" );

        this.setSeason( new Date(System.currentTimeMillis()).getYear() );
        this.setFixtureDate( new Date(System.currentTimeMillis()));
        this.setTime_occurred( new BigDecimal(1.0 ));

    }

//    private BigInteger convertToBigInteger(Object object) {
//        BigInteger value = null;
//        if (object instanceof Float) {
//            Float floatValue = (Float) object;
//            BigDecimal decimal = BigDecimal.valueOf(Math.round(floatValue));
//            value = decimal.toBigInteger();
//        } else if (object instanceof Double) {
//            Double doubleValue = (Double) object;
//            BigDecimal decimalValue = BigDecimal.valueOf(Math.round(doubleValue));
//            value =  decimalValue.toBigInteger();
//        } else if (object instanceof BigDecimal) {
//            value =  ((BigDecimal) object).round(new MathContext(0, RoundingMode.HALF_UP)).toBigInteger();
//        } else if (object instanceof Integer) {
//            value =  BigInteger.valueOf(((Integer) object).longValue());
//        }else if(object instanceof  BigInteger){
//            value = (BigInteger) object;
//        }
//        return value;
//    }

    public Integer getCount() {
        return count;
    }
    public void setCount(Integer count) {        this.count = count;    }

    @Override
    public int compareTo(StatViewCounts other) {
        return this.count.compareTo(other.getCount());
    }
}
