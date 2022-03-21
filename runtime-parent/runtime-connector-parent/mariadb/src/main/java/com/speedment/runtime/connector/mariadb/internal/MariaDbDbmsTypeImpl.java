/*
 *
 * Copyright (c) 2006-2020, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.runtime.connector.mariadb.internal;

import com.speedment.common.injector.State;
import com.speedment.common.injector.annotation.Config;
import com.speedment.common.injector.annotation.ExecuteBefore;
import com.speedment.runtime.config.Column;
import com.speedment.runtime.config.Dbms;
import com.speedment.runtime.connector.mysql.internal.MySqlDbmsMetadataHandler;
import com.speedment.runtime.connector.mysql.internal.MySqlDbmsOperationHandler;
import com.speedment.runtime.connector.mysql.internal.MySqlSpeedmentPredicateView;
import com.speedment.runtime.core.abstracts.AbstractDatabaseNamingConvention;
import com.speedment.runtime.core.component.DbmsHandlerComponent;
import com.speedment.runtime.core.component.ProjectComponent;
import com.speedment.runtime.core.component.connectionpool.ConnectionPoolComponent;
import com.speedment.runtime.core.component.transaction.TransactionComponent;
import com.speedment.runtime.core.db.*;
import com.speedment.runtime.core.db.metadata.TypeInfoMetaData;

import java.sql.Driver;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.speedment.runtime.core.db.SqlPredicateFragment.of;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;

/**
 *
 * @author Per Minborg
 * @author Emil Forslund
 */
public final class MariaDbDbmsTypeImpl implements DbmsType {

    private final DbmsTypeDefault support;
    private final DriverComponent driverComponent;
    private final MySqlDbmsMetadataHandler metadataHandler;
    private final MySqlDbmsOperationHandler operationHandler;
    private final MySqlSpeedmentPredicateView fieldPredicateView;
    private final String binaryCollationName;
    private final MariaDbNamingConvention namingConvention;
    private final MariaDbConnectionUrlGenerator connectionUrlGenerator;

    public MariaDbDbmsTypeImpl(
        final DriverComponent driverComponent,
        @Config(name = "db.mysql.binaryCollationName", value = "utf8_bin")
        final String binaryCollationName,
        @Config(name = "db.mysql.collationName", value = "utf8_general_ci")
        final String collationName,
        final ConnectionPoolComponent connectionPoolComponent,
        final DbmsHandlerComponent dbmsHandlerComponent,
        final ProjectComponent projectComponent,
        final TransactionComponent transactionComponent
    ) {
        this.driverComponent = requireNonNull(driverComponent);
        this.metadataHandler = new MySqlDbmsMetadataHandler(connectionPoolComponent, dbmsHandlerComponent, projectComponent);
        this.operationHandler = new MySqlDbmsOperationHandler(connectionPoolComponent, transactionComponent);
        this.fieldPredicateView = new MySqlSpeedmentPredicateView(binaryCollationName, collationName);
        this.binaryCollationName = requireNonNull(binaryCollationName);
        this.namingConvention = new MariaDbNamingConvention();
        this.connectionUrlGenerator = new MariaDbConnectionUrlGenerator(collationName);
        this.support = DbmsTypeDefault.create();
    }

    @ExecuteBefore(State.STOPPED)
    public void close() {
        operationHandler.close();
    }

    @Override
    public String getName() {
        return "MariaDB";
    }

    @Override
    public String getDriverManagerName() {
        return "MariaDB JDBC Driver";
    }

    @Override
    public int getDefaultPort() {
        return 3306;
    }

    @Override
    public String getDbmsNameMeaning() {
        return "The name of the MariaDB Database.";
    }

    @Override
    public boolean hasSchemaNames() {
        return false;
    }

    @Override
    public String getDriverName() {
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    public DatabaseNamingConvention getDatabaseNamingConvention() {
        return namingConvention;
    }

    @Override
    public DbmsMetadataHandler getMetadataHandler() {
        return metadataHandler;
    }

    @Override
    public DbmsOperationHandler getOperationHandler() {
        return operationHandler;
    }

    @Override
    public ConnectionUrlGenerator getConnectionUrlGenerator() {
        return connectionUrlGenerator;
    }

    @Override
    public FieldPredicateView getFieldPredicateView() {
        return fieldPredicateView;
    }

    @Override
    public String getInitialQuery() {
        return "select version() as `MariaDB version`";
    }

    @Override
    public SqlPredicateFragment getCollateFragment() {
        return of(" COLLATE " + binaryCollationName);
    }

    @Override
    public DbmsColumnHandler getColumnHandler() {
        return new DbmsColumnHandler() {
            @Override
            public Predicate<Column> excludedInInsertStatement() {
                return c -> false; // For MariaDB, even autoincrement fields are added to insert statements
            }

            @Override
            public Predicate<Column> excludedInUpdateStatement() {
                return c -> false;
            }

        };
    }

    @Override
    public boolean isSupported() {
        return isSupported(getDriverName());
    }

    @Override
    public Optional<String> getDefaultDbmsName() {
        return support.getDefaultDbmsName();
    }

    @Override
    public Set<TypeInfoMetaData> getDataTypes() {
        return support.getDataTypes();
    }

    @Override
    public String getSchemaTableDelimiter() {
        return support.getSchemaTableDelimiter();
    }

    @Override
    public Optional<String> getDefaultSchemaName() {
        return support.getDefaultSchemaName();
    }

    @Override
    public boolean hasDatabaseNames() {
        return support.hasDatabaseNames();
    }

    @Override
    public boolean hasDatabaseUsers() {
        return support.hasDatabaseUsers();
    }

    @Override
    public ConnectionType getConnectionType() {
        return support.getConnectionType();
    }

    @Override
    public String getResultSetTableSchema() {
        return support.getResultSetTableSchema();
    }

    @Override
    public SkipLimitSupport getSkipLimitSupport() {
        return support.getSkipLimitSupport();
    }

    @Override
    public String applySkipLimit(String originalSql, List<Object> params, long skip, long limit) {
        return support.applySkipLimit(originalSql, params, skip, limit);
    }

    @Override
    public SubSelectAlias getSubSelectAlias() {
        return support.getSubSelectAlias();
    }

    @Override
    public SortByNullOrderInsertion getSortByNullOrderInsertion() {
        return support.getSortByNullOrderInsertion();
    }

    private static final class MariaDbNamingConvention extends AbstractDatabaseNamingConvention {

        private static final String ENCLOSER = "`";
        private static final String QUOTE = "'";

        private static final Set<String> EXCLUDE_SET = Stream.of(
            "information_schema"
        ).collect(collectingAndThen(toSet(), Collections::unmodifiableSet));

        @Override
        public Set<String> getSchemaExcludeSet() {
            return EXCLUDE_SET;
        }

        @Override
        protected String getFieldQuoteStart() {
            return QUOTE;
        }

        @Override
        protected String getFieldQuoteEnd() {
            return QUOTE;
        }

        @Override
        protected String getFieldEncloserStart() {
            return ENCLOSER;
        }

        @Override
        protected String getFieldEncloserEnd() {
            return ENCLOSER;
        }
    }

    private static final class MariaDbConnectionUrlGenerator implements ConnectionUrlGenerator {

        private final String collationName;

        private MariaDbConnectionUrlGenerator(String collationName) {
            this.collationName = collationName;
        }

        @Override
        public String from(Dbms dbms) {
            final StringBuilder result = new StringBuilder()
                .append("jdbc:mariadb://")
                .append(dbms.getIpAddress().orElse(""));

            dbms.getPort().ifPresent(p -> result.append(":").append(p));

            result/*.append("/").append(dbms.getName()) */ // MariaDB treats this as default schema name
                .append("?useUnicode=true&characterEncoding=UTF-8")
                .append("&useServerPrepStmts=true&useCursorFetch=true")
                .append("&zeroDateTimeBehavior=convertToNull")
                .append("&sessionVariables=collation_connection=").append(collationName); // Fix #804

            return result.toString();
        }
    }

    private boolean isSupported(String driverName) {
        return driver(driverName).isPresent();
    }

    private Optional<Driver> driver(String driverName) {
        return driverComponent.driver(driverName);
    }

}
