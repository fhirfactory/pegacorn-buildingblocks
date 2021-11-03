/*
 * Copyright (c) 2021 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes;

import java.util.ArrayList;
import java.util.List;

public class CommunicateRoomAliasEventContent extends CommunicateRoomEventContent{
    private String canonicalAlias;
    private List<String> otherAliases;

    public CommunicateRoomAliasEventContent(){
        this.canonicalAlias = null;
        this.otherAliases = new ArrayList<>();
    }

    public String getCanonicalAlias() {
        return canonicalAlias;
    }

    public void setCanonicalAlias(String canonicalAlias) {
        this.canonicalAlias = canonicalAlias;
    }

    public List<String> getOtherAliases() {
        return otherAliases;
    }

    public void setOtherAliases(List<String> otherAliases) {
        this.otherAliases = otherAliases;
    }

    @Override
    public String toString() {
        return "CommunicateRoomAliasEventContent{" +
                "canonicalAlias=" + canonicalAlias +
                ", otherAliases=" + otherAliases +
                '}';
    }
}