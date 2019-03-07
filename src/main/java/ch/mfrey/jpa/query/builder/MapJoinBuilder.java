package ch.mfrey.jpa.query.builder;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import ch.mfrey.bean.ad.BeanPropertyDescriptor;
import ch.mfrey.bean.ad.ClassUtils;
import ch.mfrey.jpa.query.definition.AbstractCriteriaDefinition;

public class MapJoinBuilder implements JoinBuilder {
    @Override
    public StringBuilder buildJoin(String link, BeanPropertyDescriptor pd, String synonym, String nextSynonym) {
        StringBuilder join = new StringBuilder().append(synonym)
                .append(AbstractCriteriaDefinition.QUERY_APPEND_DOT)
                .append(pd.getName())
                .append(AbstractCriteriaDefinition.QUERY_APPEND_SPACE)
                .append(nextSynonym);
        int mapIdx = link.indexOf('[');

        ParameterizedType pt = (ParameterizedType) pd.getPropertyDescriptor().getReadMethod().getGenericReturnType();
        Class<?> typeToCheck = (Class<?>) pt.getActualTypeArguments()[0];
        if (ClassUtils.isSimpleValueType(typeToCheck)) {
            join.append(" ON key(")
                    .append(nextSynonym)
                    .append(") = '")
                    .append(link.substring(mapIdx + 1, link.indexOf(']', mapIdx)))
                    .append("'");

        }
        return join;

    }

    public boolean supports(String link, BeanPropertyDescriptor pd) {
        return Map.class.isAssignableFrom(pd.getPropertyType()) && link.indexOf('[') != -1
                && link.indexOf("[VALUE]") == -1;
    }
}
