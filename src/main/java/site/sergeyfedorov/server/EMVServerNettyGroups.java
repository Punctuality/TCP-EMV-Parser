package site.sergeyfedorov.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.sergeyfedorov.exceptions.ServerLifecycleException;

import java.io.Closeable;
import java.io.IOException;

public class EMVServerNettyGroups {

    private static final Logger logger = LoggerFactory.getLogger(EMVServerNettyGroups.class);

    @Getter
    public static class EMVServerNettyGroupsContainer implements Closeable {
        private final EventLoopGroup bossGroup;
        private final EventLoopGroup workerGroup;

        protected EMVServerNettyGroupsContainer() {
            logger.debug("Creating boss and worker EventLoopGroup");
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
        }

        @Override
        public void close() throws IOException {
            logger.debug("Shutting down boss and worker EventLoopGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static EMVServerNettyGroupsContainer groupsContainer;

    public static EMVServerNettyGroupsContainer getGroups() {
        if (groupsContainer == null) {
            // TODO Resolve thread count configuration

            groupsContainer = new EMVServerNettyGroupsContainer();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    groupsContainer.close();
                } catch (IOException e) {
                    throw new ServerLifecycleException("Failed to gracefully shutdown EventLoopGroup[s]", e);
                }
            }));
        }
        return groupsContainer;
    }
}
