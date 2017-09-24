package com.two.clarke;

import oracle.sql.Datum;
import org.springframework.data.jdbc.jms.support.oracle.DatumMapper;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDatumMapper implements DatumMapper {
    @Override
    public Datum toDatum(Object o, Connection connection) throws SQLException {
        return null;
    }

    @Override
    public Object fromDatum(Datum datum) throws SQLException {
        return null;
    }
}
