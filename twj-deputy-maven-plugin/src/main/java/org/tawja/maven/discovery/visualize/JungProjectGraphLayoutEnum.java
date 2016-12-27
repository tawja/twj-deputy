/*
 * Copyright 2016 Tawja.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tawja.maven.discovery.visualize;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbennani
 */
public enum JungProjectGraphLayoutEnum {
    KKLayout,
    FRLayout,
    CircleLayout,
    SpringLayout,
    SpringLayout2,
    ISOMLayout,
    TreeLayout,
    RadialTreeLayout;

    public static Class<? extends Layout> getLayoutClass(JungProjectGraphLayoutEnum en) {
        List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
        switch (en)
        {
            case KKLayout : return KKLayout.class;
            case FRLayout : return FRLayout.class;
            case CircleLayout : return CircleLayout.class;
            case SpringLayout : return SpringLayout.class;
            case SpringLayout2 : return SpringLayout2.class;
            case ISOMLayout : return ISOMLayout.class;
            case TreeLayout : return TreeLayout.class;
            case RadialTreeLayout : return RadialTreeLayout.class;
        }
        return null;
    }

    public Class<? extends Layout> getLayoutClass() {
        return getLayoutClass(this);
    }
}
