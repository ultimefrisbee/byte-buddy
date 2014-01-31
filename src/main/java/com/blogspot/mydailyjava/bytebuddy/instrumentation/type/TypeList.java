package com.blogspot.mydailyjava.bytebuddy.instrumentation.type;

import com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode.TypeSize;
import org.objectweb.asm.Type;

import java.util.*;

public interface TypeList extends List<TypeDescription> {

    static class ForLoadedType extends AbstractList<TypeDescription> implements TypeList {

        private final Class<?>[] type;

        public ForLoadedType(Class<?>[] type) {
            this.type = type;
        }

        @Override
        public TypeDescription get(int index) {
            return new TypeDescription.ForLoadedType(type[index]);
        }

        @Override
        public int size() {
            return type.length;
        }

        @Override
        public String[] toInternalNames() {
            String[] internalNames = new String[type.length];
            int i = 0;
            for(Class<?> aType : type) {
                internalNames[i++] = Type.getInternalName(aType);
            }
            return internalNames.length == 0 ? null : internalNames;
        }

        @Override
        public int getStackSize() {
            return TypeSize.sizeOf(Arrays.asList(type));
        }
    }

    String[] toInternalNames();

    int getStackSize();
}
