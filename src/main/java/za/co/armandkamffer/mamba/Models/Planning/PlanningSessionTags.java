package za.co.armandkamffer.mamba.Models.Planning;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlanningSessionTags<Obj, Tag> {
    private final Map<Obj, Set<Tag>> taggedObjects = new ConcurrentHashMap<>();
    private final Map<Tag, Set<Obj>> tags = new ConcurrentHashMap<>();

    public PlanningSessionTags() {}
    
    public void add(Obj object, Tag tag) {
        getTagsForObject(object).add(tag);
        getObjectsTaggedWith(tag).add(object);
    }
    
    private Set<Tag> getTagsForObject(Obj object) {
        if (!taggedObjects.containsKey(object)) {
            taggedObjects.put(object, ConcurrentHashMap.newKeySet());
        }
        return taggedObjects.get(object);
    }

    private Set<Obj> getObjectsTaggedWith(Tag tag) {
        if (!tags.containsKey(tag)) {
            tags.put(tag, ConcurrentHashMap.newKeySet());
        }
        return tags.get(tag);
    }
    
    public void removeObject(Obj object) {
        Set<Tag> tagsForObject = getTagsForObject(object);
        taggedObjects.remove(object);
        tagsForObject.forEach(tag -> {
            tags.get(tag).remove(object);
            if(tags.get(tag).size() == 0) {
                removeTag(tag);
            }
        });
    }
    
    public void removeTag(Tag tag) {
        Set<Obj> objectsWithTag = getObjectsTaggedWith(tag);
        tags.remove(tag);
        objectsWithTag.forEach(object -> taggedObjects.get(object).remove(tag));
    }
    
    public void removeTagFrom(Obj object, Tag tag) {
        getTagsForObject(object).remove(tag);
        getObjectsTaggedWith(tag).remove(object);
    }
    
    public Set<Obj> getObjectsWith(Tag tag) {
        return Collections.unmodifiableSet(getObjectsTaggedWith(tag));
    }
    
    public Set<Tag> getTagsFor(Obj object) {
        return Collections.unmodifiableSet(getTagsForObject(object));
    }
}