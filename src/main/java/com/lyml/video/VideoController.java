package com.lyml.video;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("")
public class VideoController {

    @Value("${path.upload}")
    private String path;//获取yml文件中的值

    @RequestMapping("")
    public ModelAndView index(ModelAndView modelAndView) {
        File uploadDir = new File(path);
        if(uploadDir.exists() && uploadDir.isDirectory()){
            File[] files = uploadDir.listFiles();
            List<String> paths = new ArrayList<String>();
            for(File f : files){
                if(!f.isDirectory() && (f.getName().endsWith(".m3u8") || f.getName().endsWith(".mp4"))){
                    paths.add(f.getName());
                }
            }
            modelAndView.addObject("paths", paths);
        }

        modelAndView.setViewName("/video");
        return modelAndView;
    }

    @RequestMapping("/transform")
    @ResponseBody
    public Object transfrom() {
        return new Gson().toJson(Common.transform(path));
    }
}