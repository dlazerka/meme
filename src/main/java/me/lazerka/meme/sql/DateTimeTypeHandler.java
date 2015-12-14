package me.lazerka.meme.sql;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;

/**
 * @author Dzmitry Lazerka
 */
@MappedJdbcTypes(JdbcType.TIMESTAMP)
public class DateTimeTypeHandler extends BaseTypeHandler<DateTime> {
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType) throws SQLException {
		ps.setTimestamp(i, new Timestamp(parameter.getMillis()));
	}

	@Override
	public DateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Timestamp timestamp = rs.getTimestamp(columnName);
		return new DateTime(timestamp.getTime(), DateTimeZone.UTC);
	}

	@Override
	public DateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Timestamp timestamp = rs.getTimestamp(columnIndex);
		return new DateTime(timestamp.getTime(), DateTimeZone.UTC);
	}

	@Override
	public DateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Timestamp timestamp = cs.getTimestamp(columnIndex);
		return new DateTime(timestamp.getTime(), DateTimeZone.UTC);
	}
}
