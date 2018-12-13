/**
 * Copyright (C) 2014 The Holodeck B2B Team, Sander Fieten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.holodeckb2b.ebms3.pulling.trigger.filebased;

import org.holodeckb2b.common.messagemodel.SelectivePullRequest;
import org.holodeckb2b.interfaces.messagemodel.IAgreementReference;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

/**
 * Represents the <code>AgreementRef</code> element from a Pull Request meta-data XML document.
 * <p>This class implements {@link IAgreementReference} so it can directly be used to create {@link 
 * SelectivePullRequest} instances.  
 *
 * @author Sander Fieten (sander at holodeck-b2b.org)
 */
public class AgreementReference implements IAgreementReference {

    @Text
    private String      name;

    @Attribute(name = "type", required = false)
    private String      type;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getPModeId() {
        return null;
    }  
}
