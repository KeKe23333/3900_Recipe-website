package com.yyds.recipe.service.impl;

import com.yyds.recipe.exception.response.ResponseCode;
import com.yyds.recipe.mapper.CollectionMapper;
import com.yyds.recipe.mapper.RecipeMapper;
import com.yyds.recipe.mapper.UserMapper;
import com.yyds.recipe.model.Collection;
import com.yyds.recipe.model.Recipe;
import com.yyds.recipe.model.User;
import com.yyds.recipe.service.CollectionService;
import com.yyds.recipe.utils.ResponseUtil;
import com.yyds.recipe.utils.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RecipeMapper recipeMapper;

    @Override
    public ResponseEntity<?> addCollection(String userId, String collectionName) {

        if (userId == null) {
            return ResponseUtil.getResponse(ResponseCode.USERID_NOT_FOUND_ERROR, null, null);
        }

        if (collectionName == null) {
            return ResponseUtil.getResponse(ResponseCode.BUSINESS_PARAMETER_ERROR, null, null);
        }

        User user = null;
        try {
            user = userMapper.getUserByUserId(userId);
        } catch (Exception e) {
            return ResponseUtil.getResponse(ResponseCode.DATABASE_GENERAL_ERROR, null, null);
        }

        String collectionId = UUIDGenerator.createCollectionId();
        Collection newCollection = new Collection();
        newCollection.setCollectionId(collectionId);
        newCollection.setCollectionName(collectionName);
        newCollection.setCollectorId(userId);

        user.addCollection(newCollection);

        try {
            collectionMapper.updateCollections(userId, user.getCollections());
        } catch (Exception e) {
            return ResponseUtil.getResponse(ResponseCode.DATABASE_GENERAL_ERROR, null, null);
        }

        return ResponseUtil.getResponse(ResponseCode.SUCCESS, null, null);
    }

    @Override
    public ResponseEntity<?> removeCollection(String userId, String collectionId) {
        ResponseEntity<?> error = verifyCollection(userId, collectionId);

        if (error != null) {
            return error;
        }

        User user = userMapper.getUserByUserId(userId);
        user.removeCollection(collectionId);

        try {
            collectionMapper.updateCollections(userId, user.getCollections());
        } catch (Exception e) {
            return ResponseUtil.getResponse(ResponseCode.DATABASE_GENERAL_ERROR, null, null);
        }

        return ResponseUtil.getResponse(ResponseCode.SUCCESS, null, null);

    }

    @Override
    public ResponseEntity<?> changeCollectionName(String userId, String collectionId, String collectionName) {

        ResponseEntity<?> error = verifyCollection(userId, collectionId);

        if (error != null) {
            return error;
        }

        if (collectionName == null) {
            return ResponseUtil.getResponse(ResponseCode.BUSINESS_PARAMETER_ERROR, null, null);
        }

        User user = userMapper.getUserByUserId(userId);

        HashMap<String, Collection> collections = user.getCollections();
        collections.get(collectionId).setCollectionName(collectionName);

        try {
            collectionMapper.updateCollections(userId, collections);
        } catch (Exception e) {
            return ResponseUtil.getResponse(ResponseCode.DATABASE_GENERAL_ERROR, null, null);
        }

        return ResponseUtil.getResponse(ResponseCode.SUCCESS, null, null);
    }

    @Override
    public ResponseEntity<?> addRecipeToCollection(String userId, String collectionId, String recipeId) {
        ResponseEntity<?> error = verifyCollection(userId, collectionId);

        if (error != null) {
            return error;
        }

        if (recipeId == null || recipeMapper.getRecipeById(recipeId) == null) {
            return ResponseUtil.getResponse(ResponseCode.RECIPE_ID_NOT_FOUND, null, null);
        }

        User user = userMapper.getUserByUserId(userId);
        Recipe recipe = recipeMapper.getRecipeById(recipeId);

        HashMap<String, Collection> collections = user.getCollections();
        collections.get(collectionId).addRecipe(recipe);

        try {
            collectionMapper.updateCollections(userId, collections);
        } catch (Exception e) {
            return ResponseUtil.getResponse(ResponseCode.DATABASE_GENERAL_ERROR, null, null);
        }

        return ResponseUtil.getResponse(ResponseCode.SUCCESS, null, null);

    }

    @Override
    public ResponseEntity<?> removeRecipeFromCollection(String userId, String collectionId, String recipeId) {

        ResponseEntity<?> error = verifyCollection(userId, collectionId);

        if (error != null) {
            return error;
        }

        if (recipeId == null || recipeMapper.getRecipeById(recipeId) == null) {
            return ResponseUtil.getResponse(ResponseCode.RECIPE_ID_NOT_FOUND, null, null);
        }

        User user = userMapper.getUserByUserId(userId);
        Recipe recipe = recipeMapper.getRecipeById(recipeId);

        HashMap<String, Collection> collections = user.getCollections();
        collections.get(collectionId).removeRecipe(recipe);

        try {
            collectionMapper.updateCollections(userId, collections);
        } catch (Exception e) {
            return ResponseUtil.getResponse(ResponseCode.DATABASE_GENERAL_ERROR, null, null);
        }

        return ResponseUtil.getResponse(ResponseCode.SUCCESS, null, null);
    }

    public ResponseEntity<?> verifyCollection(String userId, String collectionId) {

        if (userId == null) {
            return ResponseUtil.getResponse(ResponseCode.USERID_NOT_FOUND_ERROR, null, null);
        }

        if (collectionId == null) {
            return ResponseUtil.getResponse(ResponseCode.BUSINESS_PARAMETER_ERROR, null, null);
        }

        try {
            userMapper.getUserByUserId(userId);
        } catch (Exception e) {
            return ResponseUtil.getResponse(ResponseCode.DATABASE_GENERAL_ERROR, null, null);
        }

        return null;
    }
}
