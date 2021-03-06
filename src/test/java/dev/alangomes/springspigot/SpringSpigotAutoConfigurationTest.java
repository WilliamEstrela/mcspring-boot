package dev.alangomes.springspigot;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpringSpigotAutoConfigurationTest {

    @Mock
    private Plugin plugin;

    @Mock
    private Server server;

    @Mock
    private ConfigurableApplicationContext context;

    @Mock
    private PluginManager pluginManager;

    @InjectMocks
    private SpringSpigotAutoConfiguration startupHook;

    @Before
    public void setup() {
        when(server.getPluginManager()).thenReturn(pluginManager);

        Map<String, Listener> beans = new HashMap<>();
        beans.put("b1", mock(Listener.class));
        beans.put("b2", mock(Listener.class));
        when(context.getBeansOfType(Listener.class)).thenReturn(beans);
    }

    @Test
    public void shouldRegisterAllListeners() {
        startupHook.onStartup(null);

        verify(pluginManager, times(2)).registerEvents(notNull(), eq(plugin));
    }

}