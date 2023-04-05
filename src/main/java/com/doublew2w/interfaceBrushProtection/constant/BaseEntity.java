package com.doublew2w.interfaceBrushProtection.constant;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author DoubleW2w
 * @description
 * @created 2023/4/5 0:25
 * @project interface-brush-protection
 */
@Setter
@Getter
public class BaseEntity implements Serializable {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, true);
    }
}

