package com.github.atomishere.opforalall.service;

import com.github.atomishere.opforalall.OpForAll;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ServiceLoader {
    private final OpForAll plugin;
    private final Map<String, OFService> replacementServices = new HashMap<>();
    private final List<OFService> criticalServices = new ArrayList<>();
    private final List<OFService> nonCrtiticalServices = new ArrayList<>();
    private final List<CustomService> customServices = new ArrayList<>();

    public ServiceLoader(OpForAll plugin) {
        this.plugin = plugin;
    }

    public <T extends OFService> T loadService(Class<T> serviceClass) {
        T service = null;
        try {
            for(Constructor<?> constructor : serviceClass.getConstructors()) {
                if(constructor.getParameterCount() == 1) {
                    service = serviceClass.cast(constructor.newInstance(plugin));

                    if(replacementServices.containsKey(service.getName()) && !service.isCritical()) {
                        OFService replacementService = replacementServices.get(service.getName());
                        if(serviceClass.isInstance(replacementService)) {
                            service = serviceClass.cast(replacementService);
                        }
                    }
                } else if(constructor.getParameterCount() == 2) {
                    service = serviceClass.cast(constructor.newInstance(plugin, false));

                    if(replacementServices.containsKey(service.getName()) && !service.isCritical()) {
                        OFService replacementService = replacementServices.get(service.getName());
                        if(serviceClass.isInstance(replacementService)) {
                            service = serviceClass.cast(replacementService);
                        }
                    }
                } else {
                    plugin.getLogger().severe("Could not load Service " + serviceClass.getSimpleName() + ". Constructor is invalid!");
                    return null;
                }
            }
        } catch (IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load Service " + serviceClass.getSimpleName() + ". Constructor is private!", e);
        } catch (InstantiationException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load Service " + serviceClass.getSimpleName() + ". Class is abstract!", e);
        } catch (InvocationTargetException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load Service " + serviceClass.getSimpleName() + ". An exception in the constructor was thrown (See below for debugging details", e);
        }

        if(service == null) {
            plugin.getLogger().severe("Could not load Service " + serviceClass.getSimpleName() + ". No Constructor!");
            return null;
        }

        if(service instanceof CustomService) {
            customServices.add((CustomService) service);
            plugin.getLogger().info("Loaded custom service " + service.getName() + ", from the plugin " + ((CustomService) service).getPluginName() + ".");
            return service;
        } else if(service.isCritical()) {
            criticalServices.add(service);
        } else {
            nonCrtiticalServices.add(service);
        }

        plugin.getLogger().info("Loaded service " + service.getName());
        return service;
    }

    public void startCriticalServices() {
        for(OFService service : criticalServices) {
            startService(service);
        }
    }

    public void startNonCrticicalServices() {
        for(OFService service : nonCrtiticalServices) {
            stopService(service);
        }
    }

    public void stopCriticalServices() {
        for(OFService service : nonCrtiticalServices) {
            stopService(service);
        }
    }

    public void stopNonCriticalServices() {
        for(OFService service : nonCrtiticalServices) {
            stopService(service);
        }
    }

    public void startCustomServices(String pluginName) {
        for(CustomService service : customServices) {
            if(service.getPluginName().equals(pluginName)) {
                service.startService();
            }
        }
    }

    public void stopCustomServices(String pluginName) {
        for(CustomService service : customServices) {
            if(service.getPluginName().equals(pluginName)) {
                service.stopService();
            }
        }
    }

    public void replaceService(String serviceName, OFService service) {
        replacementServices.put(serviceName, service);
    }

    public void clearServices() {
        criticalServices.clear();
        nonCrtiticalServices.clear();
        customServices.clear();
    }

    private void startService(OFService service) {
        if(!service.started()) {
            service.startService();
            plugin.getServer().getPluginManager().registerEvents(service, plugin);
        }
    }

    private void stopService(OFService service) {
        if(service.started()) {
            service.stopService();
        }
    }
}
