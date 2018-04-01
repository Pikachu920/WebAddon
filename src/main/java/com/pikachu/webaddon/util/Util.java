package com.pikachu.webaddon.util;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.util.StringMode;
import ch.njol.util.Kleenean;
import com.pikachu.webaddon.util.scope.EffectSection;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Util {

    private static final Field KEY;

    static {
        Field _KEY = null;
        try {
            _KEY = Node.class.getDeclaredField("key");
            _KEY.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Skript.warning("Skript's node key field could not be resolved.");
        }
        KEY = _KEY;
    }

    private static final Field VARIABLE_STRING_DATA;

    static {
        Field _VARIABLE_STRING_DATA = null;
        try {
            _VARIABLE_STRING_DATA = VariableString.class.getDeclaredField("string");
            _VARIABLE_STRING_DATA.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Skript.warning("Skript's variable string data field could not be resolved.");
        }
        VARIABLE_STRING_DATA = _VARIABLE_STRING_DATA;
    }

    public static final Field VARIABLE_NAME;
    public static boolean variableNameGetterExists = Skript.methodExists(Variable.class, "getName");

    static {
        if (!variableNameGetterExists) {
            Field _VARIABLE_NAME = null;
            try {
                _VARIABLE_NAME = Variable.class.getDeclaredField("name");
                _VARIABLE_NAME.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                Skript.error("Skript's variable name field could not be resolved.");
            }
            VARIABLE_NAME = _VARIABLE_NAME;
        } else {
            VARIABLE_NAME = null;
        }
    }

    public static VariableString getVariableName(Variable<?> var) {
        if (variableNameGetterExists) {
            return var.getName();
        } else {
            try {
                return (VariableString) VARIABLE_NAME.get(var);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void setKey(Node node, String key) {
        try {
            KEY.set(node, key);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void clearSectionNode(SectionNode sectionNode) {
        List<Node> nodes = new ArrayList<>();
        for (Iterator<Node> iterator = sectionNode.iterator(); iterator.hasNext();) {
            nodes.add(iterator.next());
        }
        for (Node n : nodes) {
            sectionNode.remove(n);
        }
    }

    public static TriggerSection loadSectionNode(SectionNode node, String stringrep,
                                                 boolean printErrors, String eventName,
                                                 Class<? extends Event>... events) {

        Class<? extends Event>[] originalEvents = ScriptLoader.getCurrentEvents();
        String originalName = ScriptLoader.getCurrentEventName();
        Kleenean originalDelay = ScriptLoader.hasDelayBefore;

        ScriptLoader.hasDelayBefore = Kleenean.FALSE;
        ScriptLoader.setCurrentEvent(eventName, events);

        TriggerSection triggerSection = loadSectionNode(node, stringrep, printErrors);

        ScriptLoader.setCurrentEvent(originalName, originalEvents);
        ScriptLoader.hasDelayBefore = originalDelay;

        return triggerSection;

    }

    public static TriggerSection loadSectionNode(SectionNode node, String stringRep, boolean printErrors) {
        TriggerSection trigger;
        RetainingLogHandler errors = null;
        if (printErrors) {
            errors = SkriptLogger.startRetainingLog();
        }
        try {
            trigger = new TriggerSection(node) {
                @Override
                public String toString(Event event, boolean b) {
                    return stringRep;
                }

                @Override
                public TriggerItem walk(Event event) {
                    return walk(event, true);
                }
            };
        } finally {
            if (printErrors) {
                EffectSection.stopLog(errors);
            }
        }
        return trigger;
    }

    public static String parsePath(String path) {
        return parsePath(path, true);
    }

    public static String parsePath(String path, boolean printErrors) {
        String error = null;
        VariableString varString = VariableString.newInstance(path, StringMode.MESSAGE);
        if (varString == null) {
            return null;
        }
        if (!varString.isSimple()) {
            RetainingLogHandler errors = SkriptLogger.startRetainingLog();
            try {
                Object[] data = (Object[]) VARIABLE_STRING_DATA.get(varString);
                for (int i = 0; i < data.length; i++) {
                    if (!(data[i] instanceof String)) {
                        Expression<?> expr = ReflectionUtils.getField(data[i].getClass(), data[i], "expr");
                        if (!(expr instanceof Variable<?>)) {
                            error = "A path may only contain variables";
                            return null;
                        }
                        VariableString name = getVariableName((Variable<?>) expr);
                        if (!name.isSimple()) {
                            error = "A path variable may not contain expressions";
                            return null;
                        }
                        data[i] = ":" + name.toString(null);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } finally {
                errors.stop();
                if (printErrors && error != null) {
                    Skript.error(error);
                }
            }
        }
        return varString.toString(null);
    }

}
