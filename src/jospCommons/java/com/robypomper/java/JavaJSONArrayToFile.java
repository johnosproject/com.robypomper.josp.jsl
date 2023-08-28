/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.java;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Class utils to help store and retrieve JSON arrays from files.
 * <p>
 * This class implements also an internal cache to reduce the number of
 * read/write operations on file.
 *
 * @param <T> type of JSON array content.
 * @param <K> type of id of JSON array content.
 */
public abstract class JavaJSONArrayToFile<T, K> {

    // Internal constants

    private final Filter<T> NO_FILTER = new Filter<T>() {
        @Override
        public boolean accepted(T o) {
            return true;
        }
    };


    // Internal vars

    private final ObjectMapper jsonMapper;
    private final Class<T> typeOfT;
    private final File jsonFile;
    private int fileCount;       // count in file (for total add cacheBuffered.size())
    private T fileFirst = null;
    private T fileLast = null;
    private final List<T> cacheBuffered; // stored only on memory not on file


    // Constructor

    public JavaJSONArrayToFile(File jsonFile, Class<T> typeOfT) throws IOException {
        if (jsonFile.isDirectory())
            throw new IllegalArgumentException("File can not be a directory!");

        this.jsonMapper = JsonMapper.builder().build();
        this.typeOfT = typeOfT;
        this.jsonFile = jsonFile;

        cacheBuffered = new ArrayList<>();
        initCache();
    }


    // Memory mngm

    private void initCache() throws IOException {
        ArrayNode array = getMainNode();
        fileCount = array.size();
        if (array.size() > 0) {
            fileFirst = jsonMapper.readValue(array.get(0).traverse(), typeOfT);
            fileLast = jsonMapper.readValue(array.get(array.size() - 1).traverse(), typeOfT);
        }
    }

    private ArrayNode getMainNode() throws IOException {
        JsonNode node = null;

        if (jsonFile.exists() && jsonFile.length() > 0)
            node = jsonMapper.readTree(jsonFile);

        if (node == null)
            node = jsonMapper.createArrayNode();

        if (!node.isArray())
            throw new IOException(String.format("File '%s' is not a JSON array", jsonFile.getPath()));

        return (ArrayNode) node;
    }

    public void append(T value) {
        synchronized (cacheBuffered) {
            cacheBuffered.add(value);
        }
    }

    public void storeCache() throws IOException {
        if (cacheBuffered.size() == 0) return;

        synchronized (cacheBuffered) {
            ArrayNode array = getMainNode();

            for (T v : cacheBuffered)
                array.insertPOJO(0, v);
            fileCount += cacheBuffered.size();
            if (fileFirst == null) fileFirst = getFirstBuffered();
            fileLast = cacheBuffered.get(cacheBuffered.size() - 1);
            cacheBuffered.clear();

            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, array);
        }
    }

    public void flushCache(int count) throws IOException {
        if (cacheBuffered.size() == 0) return;
        if (count <= 0) return;

        int countAdded = 0;
        synchronized (cacheBuffered) {
            if (cacheBuffered.size() == 0) {
                //System.out.println("JavaJSONArrayToFile cacheBuffered cleaned before flush cache to file.");
                return;
            }

            ArrayNode array = getMainNode();

            for (T v : cacheBuffered) {
                countAdded++;
                array.insertPOJO(0, v);
                if (countAdded == count)
                    break;
            }

            fileCount += countAdded;
            if (fileFirst == null) fileFirst = getFirstBuffered();
            fileLast = cacheBuffered.get(countAdded - 1);

            cacheBuffered.subList(0, countAdded).clear();

            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, array);
        }

    }


    // Counts, firsts and lasts

    public int count() {
        return fileCount + cacheBuffered.size();
    }

    public int countBuffered() {
        return cacheBuffered.size();
    }

    public int countFile() {
        return fileCount;
    }

    public T getFirst() {
        if (getFirstFile() != null)
            return getFirstFile();

        return getFirstBuffered();
    }

    public T getFirstBuffered() {
        if (cacheBuffered.size() > 0)
            return cacheBuffered.get(0);
        return null;
    }

    public T getFirstFile() {
        return fileFirst;
    }

    public T getLast() {
        if (getLastBuffered() != null)
            return getLastBuffered();

        return getLastFile();
    }

    public T getLastBuffered() {
        if (cacheBuffered.size() > 0)
            return cacheBuffered.get(cacheBuffered.size() - 1);
        return null;
    }

    public T getLastFile() {
        return fileLast;
    }


    // Getters and Filters

    private T findInBuffered(K id) {
        for (T v : cacheBuffered)
            if (equalsItemIds(id, getItemId(v)))
                return v;
        return null;
    }

    private T findInBuffered(Date date) {
        for (T v : cacheBuffered)
            if (equalsItemDate(date, getItemDate(v)))
                return v;
        return null;
    }

    public List<T> getAll() throws IOException {
        return filterAll(NO_FILTER);
    }

    public List<T> getLatest(long latestCount) throws IOException {
        return filterLatest(NO_FILTER, latestCount);
    }

    public List<T> getAncient(long ancientCount) throws IOException {
        return filterAncient(NO_FILTER, ancientCount);
    }

    public List<T> getById(K fromId, K toId) throws IOException {
        return filterById(NO_FILTER, fromId, toId);
    }

    public List<T> getByData(Date fromDate, Date toDate) throws IOException {
        return filterByDate(NO_FILTER, fromDate, toDate);
    }


    // Getters and Filters: All (filtered)

    public List<T> tryAll(Filter<T> filter) {
        try {
            return filterAll(filter);
        } catch (IOException e) {
            return filterAllBuffered(filter);
        }
    }

    public List<T> filterAll(Filter<T> filter) throws IOException {
        List<T> filtered = new ArrayList<>(filterAllBuffered(filter));
        Collections.reverse(filtered);
        filtered.addAll(filterAllFile(filter));
        return filtered;
    }

    private List<T> filterAllBuffered(Filter<T> filter) {
        List<T> filtered = new ArrayList<>();

        for (T o : cacheBuffered) {
            if (filter.accepted(o))
                filtered.add(o);
        }

        return filtered;
    }

    private List<T> filterAllFile(Filter<T> filter) throws IOException {
        List<T> filtered = new ArrayList<>();

        ArrayNode array = getMainNode();
        for (Iterator<JsonNode> i = array.elements(); i.hasNext(); ) {
            JsonNode node = i.next();
            T o = jsonMapper.readValue(node.traverse(), typeOfT);

            if (filter.accepted(o))
                filtered.add(o);
        }

        return filtered;
    }


    // Getters and Filters: Latest

    public List<T> tryLatest(Filter<T> filter, long latestCount) {
        try {
            return filterLatest(filter, latestCount);
        } catch (IOException e) {
            return filterLatestBuffered(filter, latestCount);
        }
    }

    public List<T> filterLatest(Filter<T> filter, long latestCount) throws IOException {
        List<T> filtered = new ArrayList<>(filterLatestBuffered(filter, latestCount));

        if (filtered.size() < latestCount)
            filtered.addAll(filterLatestFile(filter, latestCount - filtered.size()));

        return filtered;
    }

    private List<T> filterLatestBuffered(Filter<T> filter, long latestCount) {
        List<T> filtered = new ArrayList<>();

        for (ListIterator<T> i = cacheBuffered.listIterator(cacheBuffered.size()); i.hasPrevious(); ) {
            T o = i.previous();
            if (latestCount-- == 0)
                break;
            if (filter.accepted(o))
                filtered.add(o);
        }

        return filtered;
    }

    private List<T> filterLatestFile(Filter<T> filter, long latestCount) throws IOException {
        List<T> filtered = new ArrayList<>();

        ArrayNode array = getMainNode();
        for (Iterator<JsonNode> i = array.elements(); i.hasNext(); ) {
            JsonNode node = i.next();
            T o = jsonMapper.readValue(node.traverse(), typeOfT);

            if (filter.accepted(o)) {
                filtered.add(o);
                if (--latestCount == 0)
                    break;
            }
        }

        return filtered;
    }


    // Getters and Filters: Ancient

    public List<T> tryAncient(Filter<T> filter, long ancientCount) {
        try {
            return filterAncient(filter, ancientCount);
        } catch (IOException e) {
            return filterAncientBuffered(filter, ancientCount);
        }
    }

    public List<T> filterAncient(Filter<T> filter, long ancientCount) throws IOException {
        List<T> filtered = new ArrayList<>(filterAncientFile(filter, ancientCount));

        if (filtered.size() < ancientCount)
            filtered.addAll(filterAncientBuffered(filter, ancientCount - filtered.size()));

        return filtered;
    }

    private List<T> filterAncientBuffered(Filter<T> filter, long ancientCount) {
        List<T> filtered = new ArrayList<>();

        for (T o : cacheBuffered) {
            if (ancientCount-- == 0)
                break;
            if (filter.accepted(o))
                filtered.add(o);
        }

        return filtered;
    }

    private List<T> filterAncientFile(Filter<T> filter, long ancientCount) throws IOException {
        List<T> filtered = new ArrayList<>();

        ArrayNode array = getMainNode();
        for (JsonNode jsonNode : array) {
            T o = jsonMapper.readValue(jsonNode.traverse(), typeOfT);
            if (filter.accepted(o))
                filtered.add(o);
        }

        if (filtered.size() > ancientCount)
            filtered = filtered.subList((int) (filtered.size() - ancientCount), filtered.size());

        return filtered;
    }


    // Getters and Filters: ById

    public List<T> tryById(Filter<T> filter, K fromId, K toId) {
        try {
            return filterById(filter, fromId, toId);
        } catch (IOException e) {
            return filterByIdBuffered(filter, fromId, toId);
        }
    }

    public List<T> filterById(Filter<T> filter, K fromId, K toId) throws IOException {
        if (count() == 0) return new ArrayList<>();

        // If 1stBufElem < fromId
        if (fromId != null && getFirstBuffered() != null && compareItemIds(getItemId(getFirstBuffered()), fromId) < 0)
            return filterByIdBuffered(filter, fromId, toId);

        List<T> filtered = filterByIdFile(filter, fromId, toId);
        // If LastBufElem < toId
        if (toId == null || (getLastBuffered() != null && compareItemIds(getItemId(getFirstBuffered()), toId) < 0))
            filtered.addAll(filterByIdBuffered(filter, fromId, toId));
        return filtered;
    }

    private List<T> filterByIdBuffered(Filter<T> filter, K fromId, K toId) {
        boolean store = fromId == null;
        List<T> range = new ArrayList<>();

        // Until v.id > toId
        for (T v : cacheBuffered) {
            // To exclude fromId, use > instead >=
            if (fromId != null && compareItemIds(getItemId(v), fromId) >= 0)
                store = true;
            if (toId != null && compareItemIds(getItemId(v), toId) > 0)
                break;
            if (store && filter.accepted(v))
                range.add(v);
        }

        return range;
    }

    private List<T> filterByIdFile(Filter<T> filter, K fromId, K toId) throws IOException {
        boolean store = toId == null;
        List<T> range = new ArrayList<>();

        ArrayNode array = getMainNode();
        // Until v.id < fromId
        for (Iterator<JsonNode> i = array.elements(); i.hasNext(); ) {
            JsonNode node = i.next();
            T v = jsonMapper.readValue(node.traverse(), typeOfT);

            // to exclude toID, use < instead <=
            if (toId != null && compareItemIds(getItemId(v), toId) <= 0)
                store = true;
            if (fromId != null && compareItemIds(getItemId(v), fromId) < 0)
                break;
            if (store && filter.accepted(v))
                range.add(v);
        }

        Collections.reverse(range);
        return range;
    }


    // Getters and Filters: ByDate

    public List<T> tryByDate(Filter<T> filter, Date fromDate, Date toDate) {
        try {
            return filterByDate(filter, fromDate, toDate);
        } catch (IOException e) {
            return filterByDateBuffered(filter, fromDate, toDate);
        }
    }

    public List<T> filterByDate(Filter<T> filter, Date fromDate, Date toDate) throws IOException {
        if (count() == 0) return new ArrayList<>();

        // If 1stBufElem < fromDate
        if (fromDate != null && getFirstBuffered() != null && compareItemDate(getItemDate(getFirstBuffered()), fromDate) < 0)
            return filterByDateBuffered(filter, fromDate, toDate);

        List<T> filtered = filterByDateFile(filter, fromDate, toDate);
        // If LastBufElem < toDate
        if (toDate == null || (getLastBuffered() != null && compareItemDate(getItemDate(getLastBuffered()), toDate) < 0))
            filtered.addAll(filterByDateBuffered(filter, fromDate, toDate));
        return filtered;
    }

    private List<T> filterByDateBuffered(Filter<T> filter, Date fromDate, Date toDate) {
        boolean store = fromDate == null;
        List<T> range = new ArrayList<>();

        // Until v.date > toDate
        for (T v : cacheBuffered) {
            // To exclude fromDate, use > instead >=
            if (fromDate != null && compareItemDate(getItemDate(v), fromDate) >= 0)
                store = true;
            if (toDate != null && compareItemDate(getItemDate(v), toDate) > 0)
                break;
            if (store && filter.accepted(v))
                range.add(v);
        }

        return range;
    }

    private List<T> filterByDateFile(Filter<T> filter, Date fromDate, Date toDate) throws IOException {
        boolean store = toDate == null;
        List<T> range = new ArrayList<>();

        ArrayNode array = getMainNode();
        // Until v.date < fromDate
        for (Iterator<JsonNode> i = array.elements(); i.hasNext(); ) {
            JsonNode node = i.next();
            T v = jsonMapper.readValue(node.traverse(), typeOfT);

            // to exclude toDate, use < instead <=
            if (toDate != null && compareItemDate(getItemDate(v), toDate) <= 0)
                store = true;
            if (fromDate != null && compareItemDate(getItemDate(v), fromDate) < 0)
                break;
            if (store && filter.accepted(v))
                range.add(v);
        }

        return range;
    }


    // Sub class utils

    protected abstract int compareItemIds(K id1, K id2);

    protected boolean equalsItemIds(K id1, K id2) {
        return compareItemIds(id1, id2) == 0;
    }

    protected abstract K getItemId(T value);

    protected int compareItemDate(Date id1, Date id2) {
        return id1.compareTo(id2);
    }

    protected boolean equalsItemDate(Date id1, Date id2) {
        return compareItemDate(id1, id2) == 0;
    }

    protected abstract Date getItemDate(T value);

    public interface Filter<T> {
        boolean accepted(T o);
    }

}
