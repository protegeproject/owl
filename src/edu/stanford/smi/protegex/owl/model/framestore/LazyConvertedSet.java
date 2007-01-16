package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Localizable;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;


@SuppressWarnings("unchecked")
public class LazyConvertedSet extends AbstractSet implements Localizable {
    private transient OWLFrameFactoryInvocationHandler converter;
    private Set values;
    
    public Iterator iterator() {
        return new Iterator() {
            private Iterator it = values.iterator();

            public boolean hasNext() {
                return it.hasNext();
            }

            public Object next() {
                return converter.convert(it.next());
            }

            public void remove() {
                throw new UnsupportedOperationException("Unmodifiable set");
            }

        };
    }

    @Override
    public int size() {
        return values.size();
    }

    public boolean contains(Object o) {
        return values.contains(o);
    }

    public void localize(KnowledgeBase kb) {
        FrameStoreManager fsm = ((DefaultKnowledgeBase) kb).getFrameStoreManager();
        converter = (OWLFrameFactoryInvocationHandler) 
            fsm.getFrameStoreFromClass(OWLFrameFactoryInvocationHandler.class);
    }
}
