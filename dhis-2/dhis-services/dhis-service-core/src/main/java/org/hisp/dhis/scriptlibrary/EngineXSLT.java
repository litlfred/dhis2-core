package org.hisp.dhis.scriptlibrary;
/*
 * Copyright (c) 2016, IntraHealth International
 * All rights reserved.
 * Apache 2.0
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.Reader;
import org.apache.commons.io.input.ReaderInputStream;
import java.net.URISyntaxException;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.hisp.dhis.appmanager.App;
import org.hisp.dhis.appmanager.AppManager;
import org.hisp.dhis.scriptlibrary.Engine;
import org.hisp.dhis.scriptlibrary.IExecutionContext;
import org.hisp.dhis.scriptlibrary.ScriptNotFoundException;
import org.hisp.dhis.scriptlibrary.ScriptAccessException;
import org.springframework.beans.factory.annotation.Autowired;

public class EngineXSLT extends Engine
{
    @Autowired
    protected  AppManager appManager;

    protected TransformerFactory factory = TransformerFactory.newInstance();

    public EngineXSLT ( App app, ScriptLibrary sl )
    {
        super ( app, sl );
    }

    @Override
    protected Object eval ( IExecutionContext execContext )
    throws ScriptException, ScriptNotFoundException, ScriptAccessException
    {
        Reader xslt_source;
        Transformer transformer;

        try
        {
            xslt_source = sl.retrieveScript ( execContext.getScriptName() );
            Source xslt = new StreamSource ( new ReaderInputStream ( xslt_source ) );
            Source text = new StreamSource ( execContext.getIn() );
            StreamResult trans_out = new StreamResult ( execContext.getOut() );
            transformer = factory.newTransformer ( xslt );
            transformer.setParameter ( "dhisScriptContext", execContext );
            transformer.transform ( text, trans_out );
        }

        catch ( ScriptNotFoundException ioe )
        {
            throw new ScriptException ( "Could not get source" + ioe.toString() );
        }

        catch ( Exception e )
        {
            runException = e;
            return null;
        }

        return transformer.getOutputProperties();
    }


}