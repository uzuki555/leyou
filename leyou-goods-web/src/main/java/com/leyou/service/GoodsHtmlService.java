package com.leyou.service;

import com.leyou.Utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TemplateEngine templateEngine;

    public void createHtml(Long SpuId){


        Context context = new Context();
        Map<String, Object> variables = goodsService.querySpuBySpuId(SpuId);
        context.setVariables(variables);
        PrintWriter printWriter=null;
        try {
            File file = new File("" + SpuId + ".html");
             printWriter = new PrintWriter(file);
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter!=null){
                printWriter.close();
            }
        }
    }
    public  void asyncExcute(Long SpuId){
        ThreadUtils.execute(() -> createHtml(SpuId));
    }


    public void deleteHtml(Long spuId) {
        File file = new File("" + spuId + ".html");
        file.deleteOnExit();
    }
}
