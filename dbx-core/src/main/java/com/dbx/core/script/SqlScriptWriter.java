package com.dbx.core.script;


import cn.hutool.core.io.FileUtil;
import com.dbx.core.exception.JobException;
import lombok.NonNull;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生成sql语句
 *
 * @author Aqoo
 */
public class SqlScriptWriter {

    private static final ExecutorService FILE_EXECUTOR;

    private static final Map<String, Boolean> PACKAGE_EXIST = new HashMap<>();

    private static final File LOG_DIR;

    private static final byte[] CR = System.lineSeparator().getBytes(StandardCharsets.UTF_8);

    static {
        String property = System.getProperty("dbx.log.enable");
        // 默认开启日志记录
        if (StringUtils.hasText(property)) {
            boolean out = Boolean.parseBoolean(property);
        }
        // 获取下项目根路径
        String path = System.getProperty("dbx.log.dir");
        if (!StringUtils.hasText(path)) {
            path = System.getProperty("user.dir");
        }
        path = path + File.separator + "log";
        LOG_DIR = new File(path);
        if (!LOG_DIR.exists()) {
            LOG_DIR.mkdirs();
        }
        FILE_EXECUTOR = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new MyDefaultThreadFactory()
                , new ThreadPoolExecutor.AbortPolicy());
    }


    public static String getRootPath() {
        return LOG_DIR.getAbsolutePath();
    }


    public static void writeSchemaScript(String scope, String sql) {
        if (sql == null || scope == null) {
            throw new IllegalArgumentException("ddl sql and scope must not be null");
        }
        FILE_EXECUTOR.execute(new FileWriteTask(LOG_DIR, false, scope, sql));
    }

    public static void writeDataScript(boolean mergeDataSql, String scope, String id, String... sql) {
        if (sql == null || id == null || scope == null) {
            throw new IllegalArgumentException("scope and id and sql must not be null");
        }
        FILE_EXECUTOR.execute(new FileWriteTask(LOG_DIR, mergeDataSql, scope, id, sql));
    }


    static class FileWriteTask implements Runnable {

        //public static final Map<String, Integer> ALREADY_WRITE_SQL = new ConcurrentHashMap<>();
        private final boolean mergerDataSql;
        private final File logDir;
        private final boolean ddlSql;
        private final String scope;
        private final String[] sql;
        private final String id;


        FileWriteTask(File logDir, boolean mergerDataSql, String scope, String id, String... sql) {
            this.logDir = logDir;
            this.mergerDataSql = mergerDataSql;
            this.sql = sql;
            this.scope = scope;
            this.ddlSql = false;
            this.id = id;
        }

        FileWriteTask(File logDir, boolean mergerDataSql, String scope, String sql) {
            this.logDir = logDir;
            this.mergerDataSql = mergerDataSql;
            this.sql = new String[]{sql};
            this.scope = scope;
            this.ddlSql = true;
            id = null;
        }

        private File getFile(String fileName) throws FontFormatException {
            if (!StringUtils.hasText(scope)) {
                throw new JobException("the scope is null.");
            }
            File scopePac = new File(LOG_DIR + File.separator + scope);
            Boolean aBoolean = PACKAGE_EXIST.get(scope);
            if (aBoolean == null && scopePac.exists()) {
                FileUtil.del(scopePac);
                PACKAGE_EXIST.put(scope, true);
            }
            scopePac.mkdirs();
            return new File(scopePac + File.separator + fileName);
        }

        @Override
        public void run() {
            if (ddlSql) {
                String fileName = String.format("dbx_%s_DDL.sql", scope);
                try (FileOutputStream ps = new FileOutputStream(getFile(fileName), true)) {
                    ps.write(CR);
                    ps.write(CR);
                    ps.write((sql[0] + ";").getBytes(StandardCharsets.UTF_8));
                } catch (IOException | FontFormatException e) {
                    e.printStackTrace();
                }
            } else {
                String fileName = mergerDataSql ? String.format("dbx_%s_DATA.sql", scope) : String.format("dbx_%s.sql", id);
                try (FileOutputStream ps = new FileOutputStream(getFile(fileName), true)) {
                    for (String sql : sql) {
                        ps.write((sql + ";").getBytes(StandardCharsets.UTF_8));
                        ps.write(CR);
                        ps.write(CR);
                    }
                } catch (IOException | FontFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class MyDefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        MyDefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
