package za.co.armandkamffer.mamba.Models.Planning;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Tags<U, T> {
    // Each object has a set of tags
    private final Map<U, Set<T>> taggedObjects = new ConcurrentHashMap();
    // Each tag has a set of objects
    private final Map<T, Set<U>> tags = new ConcurrentHashMap<>();

    public Tags() {
    }
    
    public void add(U object, T tag) {
        getTagsForObject(object).add(tag);
        getObjectsTaggedWith(tag).add(object);
    }
    
    private Set<T> getTagsForObject(U object) {
        if (!taggedObjects.containsKey(object)) {
            taggedObjects.put(object, ConcurrentHashMap.newKeySet());
        }
        return taggedObjects.get(object);
    }

    private Set<U> getObjectsTaggedWith(T tag) {
        if (!tags.containsKey(tag)) {
            tags.put(tag, ConcurrentHashMap.newKeySet());
        }
        return tags.get(tag);
    }
    
    public void removeObject(U object) {
        Set<T> tagsForObject = getTagsForObject(object);
        taggedObjects.remove(object);
        tagsForObject.forEach(tag -> tags.get(tag).remove(object));
    }
    
    public void removeTag(T tag) {
        Set<U> objectsWithTag = getObjectsTaggedWith(tag);
        tags.remove(tag);
        objectsWithTag.forEach(object -> taggedObjects.get(object).remove(tag));
    }
    
    public void removeTagFrom(U object, T tag) {
        getTagsForObject(object).remove(tag);
        getObjectsTaggedWith(tag).remove(object);
    }
    
    public Set<U> getObjectsWith(T tag) {
        return Collections.unmodifiableSet(getObjectsTaggedWith(tag));
    }
    
    public Set<T> getTagsFor(U object) {
        return Collections.unmodifiableSet(getTagsForObject(object));
    }
}