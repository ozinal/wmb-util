/*
 * Copyright 2007 (C) Callista Enterprise.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *	http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.googlecode.wmbutil.messages.payload;

import com.googlecode.wmbutil.CCSID;
import com.googlecode.wmbutil.NiceMbException;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;

import java.io.UnsupportedEncodingException;


/**
 * Helper class for working with BLOB messages.
 */
public class BlobPayload extends Payload {

    /**
     * Wraps a payload
     *
     * @param msg      The message containing the BLOB payload
     * @return The BLOB payload found in the message
     * @throws MbException
     */
    public static BlobPayload wrap(MbMessage msg) throws MbException {
        MbElement elm = locateBlob(msg);

        if (elm == null) {
            throw new NiceMbException("Failed to find BLOB payload");
        }

        return new BlobPayload(elm);
    }

    /**
     * Creates a payload as the last child, even if one already headerExistsIn
     *
     * @param msg The message where to create a BLOB payload
     * @return A newly created BLOB payload
     * @throws MbException
     */
    public static BlobPayload create(MbMessage msg) throws MbException {
        MbElement elm = msg.getRootElement().createElementAsLastChild(MbBLOB.PARSER_NAME);

        MbElement blobElm = elm.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BLOB", null);

        return new BlobPayload(blobElm);
    }

    /**
     * Wraps or creates a payload as the last child, even if one already headerExistsIn
     *
     * @param msg The message where to look for/create an XML payload
     * @return An BLOB payload, existent or newly created
     * @throws MbException
     */
    public static BlobPayload wrapOrCreate(MbMessage msg) throws MbException {
        if (has(msg)) {
            return wrap(msg);
        } else {
            return create(msg);
        }
    }


    /**
     * Removes (detaches) the first XML payload
     *
     * @param msg The message containing the BLOB payload
     * @return The detached payload
     * @throws MbException
     */
    public static BlobPayload remove(MbMessage msg) throws MbException {
        MbElement elm = locateBlob(msg);

        if (elm != null) {
            elm.detach();
            return new BlobPayload(elm); // TODO: Make Immutable Variant
        } else {
            throw new NiceMbException("Failed to find BLOB payload");
        }
    }

    /**
     * Checks if a message contains an BLOB payload
     *
     * @param msg The message to check
     * @return True if there's a BLOB payload in the message
     * @throws MbException
     */
    public static boolean has(MbMessage msg) throws MbException {
        MbElement elm = locateBlob(msg);
        return elm != null;
    }

    /**
     * Locates the BLOB in a message
     *
     * @param msg The message to check
     * @return The BLOB element of the message
     * @throws MbException
     */
    private static MbElement locateBlob(MbMessage msg) throws MbException {
        return msg.getRootElement().getFirstElementByPath("/BLOB/BLOB");
    }

    /**
     * Class constructor
     *
     * @param elm      The message element
     * @throws MbException
     */
    private BlobPayload(MbElement elm) throws MbException {
        super(elm);
    }

    /**
     * Gets the BLOB data
     *
     * @return The BLOB data
     * @throws MbException
     */
    public byte[] getData() throws MbException {
        return (byte[]) getMbElement().getValue();
    }

    /**
     * Sets the BLOB data
     *
     * @param b The data
     * @throws MbException
     */
    public void setData(byte[] b) throws MbException {
        getMbElement().setValue(b);
    }

    /**
     * Gets the data as a string converted using the specified ccsid
     *
     * @param ccsid The Coded Character Set Identifier to use for the conversion
     * @return
     * @throws MbException
     */
    public String getDataAsString(int ccsid) throws MbException {
        String encoding = CCSID.valueOf(ccsid).toCharsetString();
        try {
            return new String(getData(), encoding);
        } catch (UnsupportedEncodingException e) {
            throw NiceMbException.propagate(e);
        }
    }

    /**
     * Sets the data from a string converted using the specified ccsid
     *
     * @param s     The string to convert
     * @param ccsid The Coded Character Set Identifier to use for the conversion
     * @throws MbException
     */
    public void setDataAsString(String s, int ccsid) throws MbException {
        String encoding = CCSID.valueOf(ccsid).toCharsetString();
        try {
            setData(s.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw NiceMbException.propagate(e);
        }
    }

}